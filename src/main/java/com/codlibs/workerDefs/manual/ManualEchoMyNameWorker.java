package com.codlibs.workerDefs.manual;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualEchoMyNameWorker implements Worker {

    private final Logger logger = LoggerFactory.getLogger(ManualEchoMyNameWorker.class);
    private static final String MANUAL_ECHO_MY_NAME_TASK = "ManualEchoMyName";

    @Override
    public String getTaskDefName() {
        return MANUAL_ECHO_MY_NAME_TASK;
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult taskResult = new TaskResult(task);
        var name = (String) task.getInputData().get("name");
        logger.info("Invoked worker with name: {}", name);
        if (name.matches("^\\d+.*$")) {
            taskResult.addOutputData("error", "Failed; name Starts with Numeric: " + name)
                    .setStatus(TaskResult.Status.FAILED_WITH_TERMINAL_ERROR);
        } else {
            taskResult.addOutputData("greeting", "Hello " + name + "!")
                    .setStatus(TaskResult.Status.COMPLETED);
        }
        return taskResult;
    }
}
