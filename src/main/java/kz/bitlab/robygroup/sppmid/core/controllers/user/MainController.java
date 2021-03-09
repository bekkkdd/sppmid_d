package kz.bitlab.robygroup.sppmid.core.controllers.user;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Message;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.FileUploadService;
import kz.bitlab.robygroup.sppmid.core.services.MailService;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import kz.bitlab.robygroup.sppmid.core.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class MainController extends BaseController {

    @Autowired
    private MainProcessService processService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    MailService mailService;

    @GetMapping(value = "/")
    public String index(Model model) {
        Users user = this.getUserData();
        if (user != null) {
            model.addAttribute("user", user);
            return "index";
        } else {
            return "login";
        }
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        Users user = this.getUserData();
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping(value = "/resetpassword")
    public String rePasswordCode(Model model) {
        return "repassword";
    }

    @GetMapping(value = "/repassword")
    public String rePasswordCodeCode(Model model, @RequestParam(name = "email") String email) {
        model.addAttribute("email", email);
        return "repasswordCode";
    }

    @PostMapping(value = "/toresetpassword")
    public String rePassword(@RequestParam(name = "email") String email,
                             HttpServletRequest request) {
        String messageToRePassword = StringUtil.getSixDigitNumber();
        Users user = userService.findUserByEmail(email);
        if (user != null) {
            this.mailService.sendSimpleMessage(email, ("RePassword " + user.getFirstName() + " code"), messageToRePassword);
            processService.save(new Message(email, messageToRePassword));
            return "redirect:/repassword?email=" + email;
        } else {
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
    }

    @PostMapping(value = "/repasswordcheck")
    public String rePasswordCode(Model model,
                                 @RequestParam("email") String email,
                                 @RequestParam("code") String code,
                                 HttpServletRequest request) {
        if (processService.findMessageByEmail(email).getMessage().equals(code)) {
            Users user = userService.findUserByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("email", email);
            processService.deleteMessage(processService.findMessageByEmail(email));
            return "redirect:/repasswordFinal?email=" + email;
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping(value = "/repasswordFinal")
    public String rePasswordFinal(@RequestParam(name = "email") String email, Model model) {
        model.addAttribute("email", email);
        return "repasswordFinal";
    }

    @PostMapping(value = "/repasswordFinal/{email}")
    public String rePasswordFinal(@PathVariable(name = "email") String email,
                                  @RequestParam(name = "password") String password,
                                  @RequestParam(name = "repassword") String repassword) {
        Users user = userService.findUserByEmail(email);
        if (user != null && password.equals(repassword)) {
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
        }
        return "redirect:/";
    }

    @GetMapping(value = "/editprofile")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(Model model) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        return "editprofile";
    }

    @PostMapping(value = "/updatepassword")
    @PreAuthorize("isAuthenticated()")
    public String updatePassword(Model model,
                                 @RequestParam(name = "user_old_password") String oldPassword,
                                 @RequestParam(name = "user_new_password") String newPassword,
                                 @RequestParam(name = "re_user_new_password") String reNewPassword) {

        String uri = "?error";

        if (newPassword.equals(reNewPassword)) {

            Users user = this.getUserData();
            model.addAttribute("user", user);

            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.updateUser(user);
                uri = "?success";
            }
        }

        return "redirect:/editprofile" + uri;
    }

    @GetMapping(value = "/createprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String createProcess(Model model) {
        Users user = this.getUserData();
        model.addAttribute("user", user);

        List<Organizations> organizations = userService.getAllOrganizations();
        model.addAttribute("organizations", organizations);

        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        return "user/ispolnitelmid/createprocess";
    }

    @PostMapping(value = "/createprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String createProcess(
            @RequestParam(name = "currency_id") Long currencyId,
            @RequestParam(name = "organization_id") Long organizationId,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "first_year", defaultValue = "0") int firstYear,
            @RequestParam(name = "second_year", defaultValue = "0") int secondYear,
            @RequestParam(name = "third_year", defaultValue = "0") int thirdYear
    ) {

        String uri = "?error";
        Users user = this.getUserData();
        Organizations organization = userService.getOrganziationById(organizationId);
        Currencies currency = processService.getCurrency(currencyId);
        if (organization != null && currency != null) {
            processService.createProcess(new MainProcess(null, user.getDepartment().getOrganization(), organization, null, user, currency, limit, limit, firstYear, secondYear, thirdYear, StaticConfig.STATUS_UNDER_CONSIDERATION, StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION), StaticConfig.FIRST_STAGE, StaticConfig.processStageRoles.get(StaticConfig.FIRST_STAGE), StaticConfig.processStageNames.get(StaticConfig.FIRST_STAGE), 0, "", 0, 0, null, null,null));
            uri = "?success";
        }
        return "redirect:/createprocess/" + uri;
    }

    @GetMapping(value = "/processlist")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String processList(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
        Users user = this.getUserData();
        int pageCount = processService.countProcessByOrganization(user.getDepartment().getOrganization());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if (pageCountFinal == 0) {
            pageCountFinal = 1;
        }

        List<MainProcess> processList = processService.getAllPagingProcessByOrganization(user.getDepartment().getOrganization(), page, StaticConfig.pageSize);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("user", user);
        model.addAttribute("processList", processList);
        return "user/ispolnitelmid/processlist";
    }

    @GetMapping(value = "/processdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String readProcess(Model model, @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId, @PathVariable(name = "id") Long id) {

        Users user = this.getUserData();
        model.addAttribute("user", user);
        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());
        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        if(process.getStage()==StaticConfig.TENTH_STAGE&&process.getStatus()==StaticConfig.STATUS_SIGNED){

            process.setStage(StaticConfig.ELEVENTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.ELEVENTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.ELEVENTH_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        }

        Currencies currency = processService.getCurrency(currencyId);

        if (currency == null) {
            currency = processService.getCurrency(1L); // GET TENGE
        }

        int totalExpence = 0;

        if (!process.getSectionData().isEmpty()) {
            for (SectionData sectionData : process.getSectionData()) {
                if (sectionData.getCurrency().getId() != currency.getId()) {
                    double coeff = sectionData.getCurrency().getTengeBuyRatio() / currency.getTengeSellRatio();
                    sectionData.setDailyExpences((int) (sectionData.getDailyExpences() * coeff));
                    sectionData.setFareExpences((int) (sectionData.getFareExpences() * coeff));
                    sectionData.setResidenceExpences((int) (sectionData.getResidenceExpences() * coeff));
                }
                totalExpence = totalExpence + sectionData.getDailyExpences() + sectionData.getFareExpences() + sectionData.getResidenceExpences();
            }
        }

        if (process.getCurrency().getId() != currency.getId()) {
            process.setLimitValue((int) (process.getLimitValue() * process.getCurrency().getTengeBuyRatio() / currency.getTengeSellRatio()));
        }

        boolean limitExceeded = totalExpence > process.getLimitValue();

        model.addAttribute("currency", currency);
        model.addAttribute("process", process);
        model.addAttribute("limitExceeded", limitExceeded);

        return "user/ispolnitelmid/processdetails";
    }

    @PostMapping(value = "/executesign")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String executeSign(Model model,
                              @RequestParam(name = "processId") Long id,
                              @RequestParam(name = "action") String action,
                              @RequestParam(name = "name", required = false, defaultValue = "") String name,
                              @RequestParam(name = "comment", required = false, defaultValue = "") String comment) {

        String uri = "?error";

        Users user = this.getUserData();
        model.addAttribute("user", user);
        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());

        if (process.getStage() == StaticConfig.FIRST_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {
            if (action.equals("sign")) {
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");
                uri = "?success";
            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);

        } else if (process.getStage() == StaticConfig.ELEVENTH_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {

            if (action.equals("sign")) {

                process.setStage(StaticConfig.ELEVENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.ELEVENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.ELEVENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");
                uri = "?success";

            } else if (action.equals("modify")) {

                process.setStage(StaticConfig.TENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);
                uri = "?success";

            }

            process.setIsUncombinable(0);
            processService.saveProcess(process);
        }
        return "redirect:/processdetails/" + id + uri;
    }

    @PostMapping(value = "/setcurrency")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String addSectionData(HttpServletResponse response,
                                 @RequestParam(name = "currency_id") Long currencyId,
                                 @RequestParam(name = "process_id") Long processId
    ) {

        Currencies currency = processService.getCurrency(currencyId);
        if (currency != null) {
            Cookie cookie = new Cookie("currencyId", currency.getId() + "");
            cookie.setMaxAge(30 * 24 * 3600);
            response.addCookie(cookie);
        }
        return "redirect:/processdetails/" + processId;
    }

    @PostMapping(value = "/combineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String combineProcesses(HttpServletRequest request) {

        Users user = this.getUserData();

        List<MainProcess> processes = processService.getProcessListByOrganization(user.getDepartment().getOrganization());
        List<MainProcess> processesToCombine = new ArrayList<>();

        for (MainProcess proc : processes) {
            if (request.getParameter("process_" + proc.getId()) != null) {
                processesToCombine.add(proc);
            }
        }

        processService.combineProcess(processesToCombine);
        return "redirect:/processlist";
    }

    @PostMapping(value = "/uncombineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_UBP_MID')")
    public String unCombineProcesses(@RequestParam(name = "uncombine_process_id") Long processId) {

        Users user = this.getUserData();
        MainProcess process = processService.getProcess(processId, user.getDepartment().getOrganization());
        if (process != null) {
            List<MainProcess> subProcesses = processService.getProcessesByParentId(process.getId());
            processService.unCombineProcess(subProcesses, process);
        }

        return "redirect:/processlist";
    }

    @GetMapping(value = "/downloadwithouteventproof/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_DKS_MID', 'ROLE_ISPOLNITEL_DKS_MID')")
    @ResponseBody
    public ResponseEntity<Resource> getWithoutEvetProofFile(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/businessTripWithoutEventProofFiles/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removewithouteventprooffile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteWithoutEvetProofFile(@RequestParam(name = "business_trip_id") Long id) {
        Users user = getUserData();
        BusinessTrips businessTrips = businessService.getBusinessTripById(id);
        if (user != null && businessTrips != null && businessTrips.getStage() == StaticConfig.BUSINESS_TRIP_REPORTER
                && businessTrips.getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_WITHOUT_EVENT_PROOF_TO_BUSINESS_TRIP, businessTrips);
        }
        return "redirect:/zayavitelgo/completebusinesstrip/" + id;
    }

    @GetMapping(value = "/downloadticket/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_DKS_MID', 'ROLE_ISPOLNITEL_DKS_MID')")
    @ResponseBody
    public ResponseEntity<Resource> getTicketFile(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/businessTripTickets/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removeticketfile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteTicketFile(@RequestParam(name = "business_trip_id") Long id) {
        Users user = getUserData();
        BusinessTrips businessTrips = businessService.getBusinessTripById(id);
        if (user != null && businessTrips != null && businessTrips.getStage() == StaticConfig.BUSINESS_TRIP_REPORTER
                && businessTrips.getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_TICKET_TO_BUSINESS_TRIP, businessTrips);
        }
        return "redirect:/zayavitelgo/completebusinesstrip/" + id;
    }

    @GetMapping(value = "/downloadphoto3x4/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_DKS_MID', 'ROLE_ISPOLNITEL_DKS_MID')")
    @ResponseBody
    public ResponseEntity<Resource> getPhoto3x4File(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/passportRequestPhoto3x4/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removephoto3x4file")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deletePhoto3x4File(@RequestParam(name = "passport_request_id") Long id) {
        Users user = getUserData();
        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (user != null && passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_REPORTER
                && passportRequest.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_PHOTO3X4_TO_PASSPORT_REQUEST, passportRequest);
        }
        return "redirect:/zayavitelgo/completepassportrequest/" + id;
    }

    @GetMapping(value = "/downloaduds/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_DKS_MID', 'ROLE_ISPOLNITEL_DKS_MID')")
    @ResponseBody
    public ResponseEntity<Resource> getUdsFile(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/passportRequestUds/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removeudsfile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteUdsFile(@RequestParam(name = "passport_request_id") Long id) {
        Users user = getUserData();
        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (user != null && passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_REPORTER
                && passportRequest.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_UDS_TO_PASSPORT_REQUEST, passportRequest);
        }
        return "redirect:/zayavitelgo/completepassportrequest/" + id;
    }

    @GetMapping(value = "/downloadboardingpass/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_VFD', 'ROLE_ISPOLNITEL_VFD')")
    @ResponseBody
    public ResponseEntity<Resource> getBoardingPass(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/advanceAccountBoardingPass/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removeboardingpassfile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteBoardingPass(@RequestParam(name = "advance_account_id") Long id) {
        Users user = getUserData();
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (user != null && advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER
                && advanceAccount.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_BOARDING_PASS_TO_ADVANCE_ACCOUNT, advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + id;
    }

    @GetMapping(value = "/downloadarrivaldeparture/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_VFD', 'ROLE_ISPOLNITEL_VFD')")
    @ResponseBody
    public ResponseEntity<Resource> getArrivalDeparture(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/advanceAccountArrivalDeparture/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removearrivaldeparturefile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteArrivalDeparture(@RequestParam(name = "advance_account_id") Long id) {
        Users user = getUserData();
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (user != null && advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER
                && advanceAccount.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_ARRIVAL_DEPARTURE_TO_ADVANCE_ACCOUNT, advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + id;
    }

    @GetMapping(value = "/downloadbankdischarge/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_VFD', 'ROLE_ISPOLNITEL_VFD')")
    @ResponseBody
    public ResponseEntity<Resource> getBankDischarge(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/advanceAccountBankDischarge/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removebankdischargefile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteBankDischarge(@RequestParam(name = "advance_account_id") Long id) {
        Users user = getUserData();
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (user != null && advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER
                && advanceAccount.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_BANK_DISCHARGE_TO_ADVANCE_ACCOUNT, advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + id;
    }

    @GetMapping(value = "/downloadbankreceipt/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_VFD', 'ROLE_ISPOLNITEL_VFD')")
    @ResponseBody
    public ResponseEntity<Resource> getBankReceipt(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/advanceAccountBankReceipt/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removebankreceiptfile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteBankReceipt(@RequestParam(name = "advance_account_id") Long id) {
        Users user = getUserData();
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (user != null && advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER
                && advanceAccount.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_BANK_RECEIPT_TO_ADVANCE_ACCOUNT, advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + id;
    }

    @GetMapping(value = "/downloadbankfiscal/{file}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL', 'ROLE_RUKOVODITEL_GO','ROLE_OTVESTVENNIY_SEKRETAR_GO','ROLE_OTVESTVENNIY_SEKRETAR_MID'," +
            "'ROLE_RUKOVODITEL_POLIT_DEP_MID' , 'ROLE_ISPOLNITEL_POLIT_DEP_MID', 'ROLE_RUKOVODITEL_VFD', 'ROLE_ISPOLNITEL_VFD')")
    @ResponseBody
    public ResponseEntity<Resource> getBankFiscal(@PathVariable(name = "file") String file) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("static/advanceAccountBankFiscal/" + file);
        InputStream in = classPathResource.getInputStream();
        ByteArrayResource byteArrayResource = new ByteArrayResource(in.readAllBytes());
        in.close();
        return ResponseEntity.
                ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file).
                body(byteArrayResource);
    }

    @PostMapping(value = "/removebankfiscalfile")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public String deleteBankFiscal(@RequestParam(name = "advance_account_id") Long id) {
        Users user = getUserData();
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (user != null && advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER
                && advanceAccount.getBusinessTrip().getCreator().getId().equals(user.getId())) {
            fileUploadService.deleteFile(StaticConfig.FILE_BANK_FISCAL_TO_ADVANCE_ACCOUNT, advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + id;
    }

}
