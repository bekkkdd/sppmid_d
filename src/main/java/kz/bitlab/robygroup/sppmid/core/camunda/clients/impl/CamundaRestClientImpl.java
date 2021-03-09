package kz.bitlab.robygroup.sppmid.core.camunda.clients.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bitlab.robygroup.sppmid.core.camunda.models.responses.TaskListResponse;
import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.exceptions.api.CamundaJsonException;
import kz.bitlab.robygroup.sppmid.core.camunda.clients.CamundaRestClient;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@AllArgsConstructor
public class CamundaRestClientImpl implements CamundaRestClient {

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Override
    public List<TaskListResponse> getAllTasks() {
        ResponseEntity<TaskListResponse[]> responseEntity = new RestTemplate()
                .getForEntity(StaticConfig.CAMUNDA_URL + "process-definition?latest=true", TaskListResponse[].class);
        TaskListResponse[] results = responseEntity.getBody();
        return Arrays.asList(results);
    }

    @Override
    public Object executeProcess(String processId, Object request) {
        ObjectMapper mapper = new ObjectMapper();
        String requestObject = null;
        try {
            requestObject = mapper.writeValueAsString(request);
        } catch (Exception e) {
            throw CamundaJsonException.builder()
                    .message("Invalid json format")
                    .exceptionCause(e.getMessage())
                    .build();
        }
        String url = StaticConfig.CAMUNDA_URL + "process-definition/" + processId + "/submit-form";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(requestObject, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.postForEntity(url, entity, String.class);
        return responseEntity.getBody();
    }

    @Override
    public Object getProcessForm(String processId) {
        String url = StaticConfig.CAMUNDA_URL + "process-definition/" + processId + "/form-variables/";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }
}
