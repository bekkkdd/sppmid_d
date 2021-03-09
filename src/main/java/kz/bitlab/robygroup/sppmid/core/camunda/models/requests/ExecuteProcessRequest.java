package kz.bitlab.robygroup.sppmid.core.camunda.models.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExecuteProcessRequest {
    private Map<Object, Object> variables;
}
