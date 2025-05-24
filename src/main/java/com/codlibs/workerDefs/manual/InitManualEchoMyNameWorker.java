package com.codlibs.workerDefs.manual;

import com.codlibs.config.OrkesClient;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;

import java.util.List;

public class InitManualEchoMyNameWorker {

    public TaskRunnerConfigurer initWorker() {
        int threadCount = 1;
        TaskClient taskClient = new TaskClient(OrkesClient.apiClient());

        TaskRunnerConfigurer configurer =
                new TaskRunnerConfigurer.Builder(taskClient, List.of(new ManualEchoMyNameWorker()))
                        .withThreadCount(threadCount)
                        .build();
        configurer.init();
        return configurer;
    }

    public static class Runner {
        public static void main(String[] args) {
            new InitManualEchoMyNameWorker().initWorker();
        }
    }
}
