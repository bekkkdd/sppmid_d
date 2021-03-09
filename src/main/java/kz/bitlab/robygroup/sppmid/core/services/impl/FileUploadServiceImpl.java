package kz.bitlab.robygroup.sppmid.core.services.impl;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import kz.bitlab.robygroup.sppmid.core.models.processes.AdvanceAccount;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import kz.bitlab.robygroup.sppmid.core.models.processes.PassportRequest;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.FileUploadService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    BusinessService businessService;

    @Value("${file.upload-dir-business-trip-event-proof}")
    private String withoutEventProofFileFolderDir;

    @Value("${file.upload-dir-business-trip-ticket}")
    private String ticketFileFolderDir;

    @Value("${file.upload-dir-passport-request-photo3x4}")
    private String photo3x4FileFolderDir;

    @Value("${file.upload-dir-passport-request-uds}")
    private String udsFileFolderDir;

    @Value("${file.upload-dir-advance-account-boarding-pass}")
    private String boardingPassFileFolderDir;

    @Value("${file.upload-dir-advance-account-arrival-departure}")
    private String arrivalDepartureFileFolderDir;

    @Value("${file.upload-dir-advance-account-bank-discharge}")
    private String bankDischargeFileFolderDir;

    @Value("${file.upload-dir-advance-account-bank-receipt}")
    private String bankReceiptFileFolderDir;

    @Value("${file.upload-dir-advance-account-bank-fiscal}")
    private String bankFiscalFileFolderDir;

    @Override
    public String uploadFile(MultipartFile file, int uploadTo, BaseEntity entity) {
        String resultPath = null;
        if (file.getContentType() != null && uploadTo == StaticConfig.FILE_WITHOUT_EVENT_PROOF_TO_BUSINESS_TRIP) {
            if (entity instanceof BusinessTrips) {
                BusinessTrips businessTrip = (BusinessTrips) entity;
                try {
                    byte[] bytes = file.getBytes();
                    String fileName = null;
                    if (file.getContentType().equals("application/pdf")) {
                        fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_without_event_proof_pdf") + ".pdf";
                    }
                    else if (file.getContentType().equals("application/msword")) {
                        fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_without_event_proof_doc") + ".doc";
                    }
                    else if (file.getContentType().equals("application/vnd.ms-excel")) {
                        fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_without_event_proof_xls") + ".xls";
                    }
                    Path fullPath = Paths.get(withoutEventProofFileFolderDir + fileName);
                    File directory = new File(withoutEventProofFileFolderDir);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    Files.write(fullPath, bytes);
                    businessTrip.setProofFile(fileName);
                    businessService.saveBusinessTrip(businessTrip);
                    resultPath = fileName;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_TICKET_TO_BUSINESS_TRIP) {
            if (entity instanceof BusinessTrips) {
                BusinessTrips businessTrip = (BusinessTrips) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_ticket_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_ticket_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(businessTrip.getId() + "_ticket_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(ticketFileFolderDir + fileName);
                        File directory = new File(ticketFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        businessTrip.setTicketFile(fileName);
                        businessService.saveBusinessTrip(businessTrip);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_PHOTO3X4_TO_PASSPORT_REQUEST) {
            if (entity instanceof PassportRequest) {
                PassportRequest passportRequest = (PassportRequest) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_photo3x4_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_photo3x4_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_photo3x4_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(photo3x4FileFolderDir + fileName);
                        File directory = new File(photo3x4FileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        passportRequest.setPhoto3x4File(fileName);
                        businessService.savePassportRequest(passportRequest);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_UDS_TO_PASSPORT_REQUEST) {
            if (entity instanceof PassportRequest) {
                PassportRequest passportRequest = (PassportRequest) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_uds_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_uds_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(passportRequest.getId() + "_uds_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(udsFileFolderDir + fileName);
                        File directory = new File(udsFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        passportRequest.setUdsFile(fileName);
                        businessService.savePassportRequest(passportRequest);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_BOARDING_PASS_TO_ADVANCE_ACCOUNT) {
            if (entity instanceof AdvanceAccount) {
                AdvanceAccount advanceAccount = (AdvanceAccount) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_boarding_pass_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_boarding_pass_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_boarding_pass_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(boardingPassFileFolderDir + fileName);
                        File directory = new File(boardingPassFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        advanceAccount.setBoardingPassFile(fileName);
                        businessService.saveAdvanceAccount(advanceAccount);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_ARRIVAL_DEPARTURE_TO_ADVANCE_ACCOUNT) {
            if (entity instanceof AdvanceAccount) {
                AdvanceAccount advanceAccount = (AdvanceAccount) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_arrival_departure_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_arrival_departure_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_arrival_departure_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(arrivalDepartureFileFolderDir + fileName);
                        File directory = new File(arrivalDepartureFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        advanceAccount.setArrivalDepartureFile(fileName);
                        businessService.saveAdvanceAccount(advanceAccount);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_BANK_DISCHARGE_TO_ADVANCE_ACCOUNT) {
            if (entity instanceof AdvanceAccount) {
                AdvanceAccount advanceAccount = (AdvanceAccount) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_discharge_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_discharge_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_discharge_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(bankDischargeFileFolderDir + fileName);
                        File directory = new File(bankDischargeFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        advanceAccount.setBankDischargeFile(fileName);
                        businessService.saveAdvanceAccount(advanceAccount);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_BANK_RECEIPT_TO_ADVANCE_ACCOUNT) {
            if (entity instanceof AdvanceAccount) {
                AdvanceAccount advanceAccount = (AdvanceAccount) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_receipt_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_receipt_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_receipt_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(bankReceiptFileFolderDir + fileName);
                        File directory = new File(bankReceiptFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        advanceAccount.setBankReceiptFile(fileName);
                        businessService.saveAdvanceAccount(advanceAccount);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (file.getContentType() != null && uploadTo == StaticConfig.FILE_BANK_FISCAL_TO_ADVANCE_ACCOUNT) {
            if (entity instanceof AdvanceAccount) {
                AdvanceAccount advanceAccount = (AdvanceAccount) entity;
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = null;
                        if (file.getContentType().equals("application/pdf")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_fiscal_pdf") + ".pdf";
                        }
                        else if (file.getContentType().equals("application/msword")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_fiscal_doc") + ".doc";
                        }
                        else if (file.getContentType().equals("application/vnd.ms-excel")) {
                            fileName = DigestUtils.sha1Hex(advanceAccount.getId() + "_bank_fiscal_xls") + ".xls";
                        }
                        else {
                            return null;
                        }
                        Path fullPath = Paths.get(bankFiscalFileFolderDir + fileName);
                        File directory = new File(bankFiscalFileFolderDir);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        Files.write(fullPath, bytes);
                        advanceAccount.setBankFiscalFile(fileName);
                        businessService.saveAdvanceAccount(advanceAccount);
                        resultPath = fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
        return resultPath;
    }

    @Override
    public void deleteFile(int deleteFrom, BaseEntity entity) {
        if (deleteFrom == StaticConfig.FILE_WITHOUT_EVENT_PROOF_TO_BUSINESS_TRIP) {
            BusinessTrips businessTrip = (BusinessTrips) entity;
            try {
                FileUtils.forceDelete(new File(withoutEventProofFileFolderDir + businessTrip.getProofFile()));
                businessTrip.setProofFile(null);
                businessService.saveBusinessTrip(businessTrip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_TICKET_TO_BUSINESS_TRIP) {
            BusinessTrips businessTrip = (BusinessTrips) entity;
            try {
                FileUtils.forceDelete(new File(ticketFileFolderDir + businessTrip.getTicketFile()));
                businessTrip.setTicketFile(null);
                businessService.saveBusinessTrip(businessTrip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_PHOTO3X4_TO_PASSPORT_REQUEST) {
            PassportRequest passportRequest = (PassportRequest) entity;
            try {
                FileUtils.forceDelete(new File(photo3x4FileFolderDir + passportRequest.getPhoto3x4File()));
                passportRequest.setPhoto3x4File(null);
                businessService.savePassportRequest(passportRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_UDS_TO_PASSPORT_REQUEST) {
            PassportRequest passportRequest = (PassportRequest) entity;
            try {
                FileUtils.forceDelete(new File(udsFileFolderDir + passportRequest.getUdsFile()));
                passportRequest.setUdsFile(null);
                businessService.savePassportRequest(passportRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_BOARDING_PASS_TO_ADVANCE_ACCOUNT) {
            AdvanceAccount advanceAccount = (AdvanceAccount) entity;
            try {
                FileUtils.forceDelete(new File(boardingPassFileFolderDir + advanceAccount.getBoardingPassFile()));
                advanceAccount.setBoardingPassFile(null);
                businessService.saveAdvanceAccount(advanceAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_ARRIVAL_DEPARTURE_TO_ADVANCE_ACCOUNT) {
            AdvanceAccount advanceAccount = (AdvanceAccount) entity;
            try {
                FileUtils.forceDelete(new File(arrivalDepartureFileFolderDir + advanceAccount.getArrivalDepartureFile()));
                advanceAccount.setArrivalDepartureFile(null);
                businessService.saveAdvanceAccount(advanceAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_BANK_DISCHARGE_TO_ADVANCE_ACCOUNT) {
            AdvanceAccount advanceAccount = (AdvanceAccount) entity;
            try {
                FileUtils.forceDelete(new File(bankDischargeFileFolderDir + advanceAccount.getBankDischargeFile()));
                advanceAccount.setBankDischargeFile(null);
                businessService.saveAdvanceAccount(advanceAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_BANK_RECEIPT_TO_ADVANCE_ACCOUNT) {
            AdvanceAccount advanceAccount = (AdvanceAccount) entity;
            try {
                FileUtils.forceDelete(new File(bankReceiptFileFolderDir + advanceAccount.getBankReceiptFile()));
                advanceAccount.setBankReceiptFile(null);
                businessService.saveAdvanceAccount(advanceAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (deleteFrom == StaticConfig.FILE_BANK_FISCAL_TO_ADVANCE_ACCOUNT) {
            AdvanceAccount advanceAccount = (AdvanceAccount) entity;
            try {
                FileUtils.forceDelete(new File(bankFiscalFileFolderDir + advanceAccount.getBankFiscalFile()));
                advanceAccount.setBankFiscalFile(null);
                businessService.saveAdvanceAccount(advanceAccount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
