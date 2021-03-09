package kz.bitlab.robygroup.sppmid.core.controllers.advice;

import kz.bitlab.robygroup.sppmid.core.camunda.controllers.BaseRestController;
import kz.bitlab.robygroup.sppmid.core.exceptions.api.CamundaJsonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Assylkhan
 * on 12.01.2020
 * @project sppmid
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends BaseRestController {

    @ExceptionHandler(CamundaJsonException.class)
    public ResponseEntity<?> jsonExceptionHandler(Object object) {
        return makeResponse(object, HttpStatus.BAD_REQUEST);
    }

}
