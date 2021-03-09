package kz.bitlab.robygroup.sppmid.core.camunda.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignDocumentRequest {

    @NotNull
    private MultipartFile file;

    @NotNull
    private String password;

}
