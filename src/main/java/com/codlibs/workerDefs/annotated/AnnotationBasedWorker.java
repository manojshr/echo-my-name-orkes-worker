package com.codlibs.workerDefs.annotated;

import com.codlibs.config.OrkesClient;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationBasedWorker {

    private final Logger logger = LoggerFactory.getLogger(AnnotationBasedWorker.class);

    public WorkflowExecutor initWorker() {
        int pollingInterval = 50;
        var executor = new WorkflowExecutor(OrkesClient.apiClient(), pollingInterval);
        String workerPackagesToScan = String.join(", ", AnnotationBasedWorker.class.getPackageName());
        executor.initWorkers(workerPackagesToScan);
        logger.info("Initialized Annotation Based Worker for packages: {} with polling interval: {} ms",
                workerPackagesToScan, pollingInterval);
        return executor;
    }

    public static class Runner {
        public static void main(String[] args) {
            new AnnotationBasedWorker().initWorker();
        }
    }
}