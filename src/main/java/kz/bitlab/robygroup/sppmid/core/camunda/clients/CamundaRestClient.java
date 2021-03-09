package kz.bitlab.robygroup.sppmid.core.camunda.clients;

import kz.bitlab.robygroup.sppmid.core.camunda.models.responses.TaskListResponse;

import java.util.List;

public interface CamundaRestClient {

    List<TaskListResponse> getAllTasks();

    Object getProcessForm(String processId);

    Object executeProcess(String processId, Object request) ;
}
