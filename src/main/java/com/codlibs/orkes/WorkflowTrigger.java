package com.codlibs.orkes;

import com.codlibs.config.OrkesClient;
import com.codlibs.utils.EnvUtil;
import com.codlibs.workerDefs.annotated.AnnotationBasedWorker;
import com.codlibs.workerDefs.manual.InitManualEchoMyNameWorker;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkflowTrigger {

    private final Logger logger = LoggerFactory.getLogger(WorkflowTrigger.class);

    private final static String WORKFLOW_NAME = EnvUtil.get("WORKFLOW_NAME", "EchoMyNameWorkflow");

    private void initWorkers(AtomicBoolean lock) {
        logger.info("Initializing Workers");
        var manualWorkerConfigurer = new InitManualEchoMyNameWorker().initWorker();
        var annotationBasedWorkerExecutor = new AnnotationBasedWorker().initWorker();
        registerAutomaticShutdown(lock, manualWorkerConfigurer, annotationBasedWorkerExecutor, 30);
        logger.info("Shutdown signal received, exiting...");
    }

    private void registerAutomaticShutdown(AtomicBoolean lock,
                                           TaskRunnerConfigurer manualWorkerConfigurer,
                                           WorkflowExecutor annotationBasedWorkerExecutor,
                                           int shutdownDelaySeconds) {
        var shutDownThread = new Thread(() -> {
            sleep(shutdownDelaySeconds);
            lock.set(false);
        });
        shutDownThread.setDaemon(true);
        shutDownThread.start();

        //Just wait until signal is received to exit
        while (lock.get()) {
            sleep(2);
            logger.debug("Waiting for shutdown signal...");
        }
        manualWorkerConfigurer.shutdown();
        annotationBasedWorkerExecutor.shutdown();
    }

    public String triggerWorkflow() {
        logger.info("Triggering workflow: {}", WORKFLOW_NAME);
        var request = new StartWorkflowRequest()
                .withName(WORKFLOW_NAME)
                .withInput(Map.of("name", "John Doe"));
        var workflowId = OrkesClient.workflowClient().startWorkflow(request);
        logger.info("Workflow triggered with ID: {}", workflowId);
        return workflowId;
    }

    private void exitOnceWorkflowCompleted(AtomicBoolean lock, String workflowId) {
        logger.info("Waiting for workflow to complete: {}; id: {}", WORKFLOW_NAME, workflowId);
        startWorkflowTimeoutThread(lock, workflowId, 30);
        var workflowClient = OrkesClient.workflowClient();
        while(lock.get()) {
            sleep(2);
            var workflowStatus = workflowClient.getWorkflow(workflowId, false);
            logger.info("workflow: {}; id:{}; status: {}", WORKFLOW_NAME, workflowId, workflowStatus.getStatus());
            if (workflowStatus.getStatus() != Workflow.WorkflowStatus.RUNNING) {
                lock.set(false);
            }
        }
        logger.info("Workflow completed, exiting...");
    }

    private void startWorkflowTimeoutThread(AtomicBoolean lock, String workflowId, int timeoutSeconds) {
        Thread workflowCompleted = new Thread(() -> {
            sleep(timeoutSeconds);
            OrkesClient.workflowClient().terminateWorkflow(workflowId, "Timeout!");
            lock.set(false);
        });
        workflowCompleted.setDaemon(true);
        workflowCompleted.start();
    }

    void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Runner {
        public static void main(String[] args) {
            final AtomicBoolean lock = new AtomicBoolean(true);
            WorkflowTrigger workflowTrigger = new WorkflowTrigger();
            var initWorkersThread = new Thread(() -> workflowTrigger.initWorkers(lock));
            initWorkersThread.setDaemon(true);
            initWorkersThread.start();
            var workflowId = workflowTrigger.triggerWorkflow();
            workflowTrigger.exitOnceWorkflowCompleted(lock, workflowId);
            System.out.println("Exiting...");
        }
    }
}
