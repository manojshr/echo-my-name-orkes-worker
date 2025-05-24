package com.codlibs.config;

import com.codlibs.utils.EnvUtil;
import com.netflix.conductor.client.http.WorkflowClient;
import io.orkes.conductor.client.ApiClient;

public class OrkesClient {
    private static final String CONDUCTOR_SERVER = EnvUtil.get("ORKES_API_URL", "https://developer.orkescloud.com/api");
    private static final String ORKES_API_KEY = EnvUtil.get("ORKES_API_KEY");
    private static final String ORKES_API_SECRET =  EnvUtil.get("ORKES_API_SECRET");

    private static ApiClient apiClient;

    private static WorkflowClient workflowClient;

    public static ApiClient apiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient(CONDUCTOR_SERVER, ORKES_API_KEY, ORKES_API_SECRET);
        }
        return apiClient;
    }

    public static WorkflowClient workflowClient() {
        if (workflowClient == null) {
            workflowClient = new WorkflowClient(apiClient());
        }
        return workflowClient;
    }
}
