package com.codlibs.workerDefs.annotated;

import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EchoMyNameWorker {

    private Logger logger = LoggerFactory.getLogger(EchoMyNameWorker.class);

    private static final String ECHO_MY_NAME_TASK = "EchoMyName";

    @WorkerTask(ECHO_MY_NAME_TASK)
    public Map<String, String> greeting(@InputParam("name") String name) {
        logger.info("Initiating Task: {}", ECHO_MY_NAME_TASK);
        if (name.matches("^\\d+.*$")) {
            return Map.of("error", "Failed; name Starts with Numeric: " + name);
        } else {
            return Map.of("greeting", "Hello " + name + "!");
        }
    }
}
