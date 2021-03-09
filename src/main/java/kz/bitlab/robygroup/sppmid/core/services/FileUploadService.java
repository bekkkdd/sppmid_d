package kz.bitlab.robygroup.sppmid.core.services;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file, int uploadTo, BaseEntity entity);
    void deleteFile(int deleteFrom, BaseEntity entity);
}
