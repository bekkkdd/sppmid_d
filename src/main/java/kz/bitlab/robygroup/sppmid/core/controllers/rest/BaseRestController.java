package kz.bitlab.robygroup.sppmid.core.controllers.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseRestController {

    public ResponseEntity<?> makeResponse(Object object, HttpStatus httpStatus){
        return new ResponseEntity(object, httpStatus);
    }

}
