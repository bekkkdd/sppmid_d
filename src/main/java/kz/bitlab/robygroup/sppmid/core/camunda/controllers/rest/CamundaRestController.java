package kz.bitlab.robygroup.sppmid.core.camunda.controllers.rest;

import kz.bitlab.robygroup.sppmid.core.camunda.controllers.BaseRestController;
import kz.bitlab.robygroup.sppmid.core.camunda.clients.CamundaRestClient;
import kz.bitlab.robygroup.sppmid.core.camunda.models.requests.SignDocumentRequest;
import kz.bitlab.robygroup.sppmid.core.services.EDSService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/camunda")
@AllArgsConstructor
public class CamundaRestController extends BaseRestController {

    private CamundaRestClient camundaRestClient;
    private EDSService edsService;

    @GetMapping("/tasks")
    public ResponseEntity<?> index() {
        return this.makeResponse(camundaRestClient.getAllTasks(), HttpStatus.OK);
    }

    @PostMapping("/execute/process/{processId}")
    public ResponseEntity<?> execute(@PathVariable String processId, @RequestBody Object request) {
        return this.makeResponse(camundaRestClient.executeProcess(processId, request), HttpStatus.OK);
    }

    @GetMapping("/prepare/process/{processId}")
    public ResponseEntity<?> prepare(@PathVariable String processId) {
        return this.makeResponse(camundaRestClient.getProcessForm(processId), HttpStatus.OK);
    }

    @PostMapping("/sign/document")
    public ResponseEntity<?> checkDocument(@ModelAttribute SignDocumentRequest signDocumentRequest) throws Exception {
        return this.makeResponse(this.edsService.getEdsUserFromFile(
                signDocumentRequest.getFile().getInputStream(), signDocumentRequest.getPassword()),
                HttpStatus.OK);
    }

}
