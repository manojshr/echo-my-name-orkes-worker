package com.codlibs.workerDefs.annotated;

import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

import java.util.Map;

public class LogResultsWorker {
    private final static String ECHO_MY_NAME_LOGGING_TASK = "EchoMyNameLogResults";

    @WorkerTask(ECHO_MY_NAME_LOGGING_TASK)
    public Map<String, Object> logging(@InputParam("echo_my_name_output") Map<String, Object> echoMyNameOutput,
                       @InputParam("manual_echo_my_name_output") Map<String, Object> manualEchoMyNameOutput) {
        return Map.of(
                "result", "Finished logging results",
                "echo_my_name.output", echoMyNameOutput,
                "manual_echo_my_name.output", manualEchoMyNameOutput
        );
    }
}
