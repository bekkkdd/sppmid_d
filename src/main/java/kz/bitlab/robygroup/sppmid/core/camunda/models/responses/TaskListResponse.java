package kz.bitlab.robygroup.sppmid.core.camunda.models.responses;

import lombok.Data;

@Data
public class TaskListResponse {
    private String id;
    private String key;
    private String category;
    private String description = null;
    private String name;
    private float version;
    private String resource;
    private String deploymentId;
    private String diagram = null;
    private boolean suspended;
    private String tenantId = null;
    private String versionTag;
    private float historyTimeToLive;
    private boolean startableInTasklist;
}