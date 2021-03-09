package kz.bitlab.robygroup.sppmid.core.camunda.models.responses;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ProcessFormResponse {
    private Map<Object, Object> variables;
}
