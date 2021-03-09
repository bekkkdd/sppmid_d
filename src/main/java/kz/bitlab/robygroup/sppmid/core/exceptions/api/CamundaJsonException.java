package kz.bitlab.robygroup.sppmid.core.exceptions.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Assylkhan
 * on 12.01.2020
 * @project sppmid
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamundaJsonException extends RuntimeException {

    private String message;
    private String exceptionCause;

}
