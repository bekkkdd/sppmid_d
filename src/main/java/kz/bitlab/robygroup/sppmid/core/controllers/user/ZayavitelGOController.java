package kz.bitlab.robygroup.sppmid.core.controllers.user;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.dto.BusinessTripDto;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.FileUploadService;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/zayavitelgo")
public class ZayavitelGOController extends BaseController {

    @Autowired
    MainProcessService processService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/processlist")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String processList(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {

        Users user = this.getUserData();

        int pageCount = processService.countProcessByDepartment(user.getDepartment());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if (pageCountFinal == 0) {
            pageCountFinal = 1;
        }

        List<MainProcess> processList = processService.getProcessListByDepartment(user.getDepartment(), page, StaticConfig.pageSize);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("processList", processList);

        return "user/zayavitelgo/processlist";
    }

    @GetMapping(value = "/processdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String readProcess(Model model, @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId, @PathVariable(name = "id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(id, currentUser.getDepartment().getOrganization());

        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        if (process.getStage() == StaticConfig.FIFTH_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {
            process.setStage(StaticConfig.SIXTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SIXTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SIXTH_STAGE));
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

        return "user/zayavitelgo/processdetails";
    }

    @PostMapping(value = "/executesign")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String executeSign(Model model,
                              @RequestParam(name = "processId") Long id,
                              @RequestParam(name = "action") String action,
                              @RequestParam(name = "comment", required = false, defaultValue = "") String comment) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(id, currentUser.getDepartment().getOrganization());

        if (process.getStage() == StaticConfig.SIXTH_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {
            if (action.equals("sign")) {

                process.setStage(StaticConfig.SIXTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SIXTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SIXTH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);
        }
        return "redirect:/zayavitelgo/processdetails/" + id + "?success";
    }

    @PostMapping(value = "/signbusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String signBusinessTrip(Model model,
                                   @RequestParam(name = "business_trip_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null && businessTrip.getCreator().getId().equals(currentUser.getId()) && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_REPORTER) {
            businessTrip.setStage(StaticConfig.BUSINESS_TRIP_GO_SUPERVISOR);
            businessService.saveBusinessTrip(businessTrip);
            return "redirect:/zayavitelgo/readbusinesstrip/" + businessTrip.getId();
        }
        return "redirect:/zayavitelgo/businesstrips";

    }

    @PostMapping(value = "/signpassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String signPassportRequest(Model model,
                                   @RequestParam(name = "passport_request_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (passportRequest != null
                && passportRequest.getRequester().getId().equals(currentUser.getId())
                && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_REPORTER) {
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_GO_SUPERVISOR);
            businessService.savePassportRequest(passportRequest);
            return "redirect:/zayavitelgo/readpassportrequest/" + passportRequest.getId();
        } else if(passportRequest != null
                && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_DKS_EXECUTOR_READY){
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_REPORTER_TOOK);
            businessService.savePassportRequest(passportRequest);
            return "redirect:/zayavitelgo/readpassportrequest/" + passportRequest.getId();
        }
        return "redirect:/zayavitelgo/passports";

    }

    @PostMapping(value = "/signadvanceaccount")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String signAdvanceAccount(Model model,
                                   @RequestParam(name = "advance_account_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (advanceAccount != null
                && advanceAccount.getRequester().getId().equals(currentUser.getId())
                && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER) {
            advanceAccount.setStage(StaticConfig.ADVANCE_ACCOUNT_VFD_EXECUTOR);
            businessService.saveAdvanceAccount(advanceAccount);
            return "redirect:/zayavitelgo/readadvanceaccount/" + advanceAccount.getId();
        }
        return "redirect:/zayavitelgo/passports";

    }


    @PostMapping(value = "/addsectiondata")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String addSectionData(Model model,
                                 @RequestParam(name = "processId") Long id,
                                 @RequestParam(name = "type") int type,
                                 @RequestParam(name = "name") String name,
                                 @RequestParam(name = "country") String country,
                                 @RequestParam(name = "limitTimeFrom") String limitTimeFrom,
                                 @RequestParam(name = "limitTimeTo") String limitTimeTo,
                                 @RequestParam(name = "participantAmount") int participantAmount,
                                 @RequestParam(name = "daysAmount") int daysAmount,
                                 @RequestParam(name = "dailyExpences") int dailyExpences,
                                 @RequestParam(name = "residenceExpences") int residenceExpences,
                                 @RequestParam(name = "fareExpences") int fareExpences,
                                 @RequestParam(name = "currency_id") Long currencyId
    ) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(id, currentUser.getDepartment().getOrganization());

        Set<SectionData> sectionDataList = process.getSectionData();
        if (sectionDataList == null) {
            sectionDataList = new HashSet<>();
        }

        Currencies currency = processService.getCurrency(currencyId);

        SectionData sectionData = processService.addSectionData(new SectionData(type, process.getId(), process.getId(), name, country, limitTimeFrom, limitTimeTo, participantAmount, daysAmount, currency, dailyExpences, residenceExpences, fareExpences));
        sectionDataList.add(sectionData);

        process.setSectionData(sectionDataList);
        processService.saveProcess(process);

        return "redirect:/zayavitelgo/processdetails/" + id;

    }

    @PostMapping(value = "/removesectiondata")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String removeSectionData(Model model,
                                    @RequestParam(name = "processId") Long processId,
                                    @RequestParam(name = "sectionId") Long sectionId
    ) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(processId, currentUser.getDepartment().getOrganization());

        Set<SectionData> sectionDataList = process.getSectionData();
        if (sectionDataList == null) {
            sectionDataList = new HashSet<>();
        }

        for (SectionData sec : sectionDataList) {
            if (sec.getId() == sectionId) {
                sectionDataList.remove(sec);
                break;
            }
        }

        process.setSectionData(sectionDataList);
        processService.saveProcess(process);

        return "redirect:/zayavitelgo/processdetails/" + processId;

    }


    @PostMapping(value = "/setcurrency")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String setCurrency(HttpServletResponse response,
                              @RequestParam(name = "currency_id") Long currencyId,
                              @RequestParam(name = "process_id") Long processId
    ) {

        Currencies currency = processService.getCurrency(currencyId);
        if (currency != null) {
            Cookie cookie = new Cookie("currencyId", currency.getId() + "");
            cookie.setMaxAge(30 * 24 * 3600);
            response.addCookie(cookie);
        }
        return "redirect:/zayavitelgo/processdetails/" + processId;
    }

    @PostMapping(value = "/combineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String combineProcesses(HttpServletRequest request) {

        Users user = this.getUserData();

        List<MainProcess> processes = processService.getProcessListByDepartment(user.getDepartment());
        List<MainProcess> processesToCombine = new ArrayList<>();

        for (MainProcess proc : processes) {
            if (request.getParameter("process_" + proc.getId()) != null) {
                processesToCombine.add(proc);
            }
        }

        processService.combineProcess(processesToCombine);
        return "redirect:/zayavitelgo/processlist";
    }

    @PostMapping(value = "/uncombineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String unCombineProcesses(@RequestParam(name = "uncombine_process_id") Long processId) {

        Users user = this.getUserData();
        MainProcess process = processService.getProcessByGosOrgan(processId, user.getDepartment().getOrganization());
        if (process != null) {
            List<MainProcess> subProcesses = processService.getProcessesByParentId(process.getId());
            processService.unCombineProcess(subProcesses, process);
        }

        return "redirect:/zayavitelgo/processlist";
    }

    @GetMapping(value = "/createbusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String createBusinessTrip(Model model) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        return "user/zayavitelgo/createbusinesstrip";
    }

    @GetMapping(value = "/createpassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String createPassportRequest(Model model) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        model.addAttribute("businessTrips", businessService.getBusinessTripsByCreatorAndStage(user, StaticConfig.BUSINESS_TRIP_ORDERED));

        return "user/zayavitelgo/createpassportrequests";
    }

    @PostMapping(value = "/createbusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String toCreateBusinessTrip() {

        Users user = this.getUserData();
        BusinessTrips businessTrips = new BusinessTrips();
        businessTrips.setCreator(user);
        businessTrips.setStage(StaticConfig.BUSINESS_TRIP_REPORTER);

        businessTrips = businessService.createBusinessTrip(businessTrips);
        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTrips.getId();

    }

    @PostMapping(value = "/createpassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String createPassportRequest(
            @RequestParam(name = "business_trip_id") Long businessTripId
    ) {

        Users user = this.getUserData();
        BusinessTrips businessTrips = businessService.getBusinessTripById(businessTripId);
        PassportRequest passportRequest = businessService.getPassportRequestByBusinessTrip(businessTrips);

        if(businessTrips != null && businessTrips.getStage() == StaticConfig.BUSINESS_TRIP_ORDERED
        && passportRequest == null){
            passportRequest = new PassportRequest();
            passportRequest.setBusinessTrip(businessTrips);
            passportRequest.setRequester(user);
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_REPORTER);
            businessService.createPassportRequest(passportRequest);
        }
        return "redirect:/zayavitelgo/passports";
    }

    @PostMapping(value = "/createadvanceaccount")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String createAdvanceAccount(
            @RequestParam(name = "business_trip_id") Long businessTripId
    ) {

        Users user = this.getUserData();
        BusinessTrips businessTrips = businessService.getBusinessTripById(businessTripId);
        AdvanceAccount advanceAccount = businessService.getAdvanceAccountByBusinessTrip(businessTrips);

        if(businessTrips != null && businessTrips.getStage() == StaticConfig.BUSINESS_TRIP_ORDERED
        && advanceAccount == null){
            advanceAccount = new AdvanceAccount();
            advanceAccount.setBusinessTrip(businessTrips);
            advanceAccount.setRequester(user);
            advanceAccount.setStage(StaticConfig.ADVANCE_ACCOUNT_REPORTER);
            advanceAccount = businessService.createAdvanceAccount(advanceAccount);
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + advanceAccount.getId();
    }

    @PostMapping(value = "/addusertobusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String toAddUserToBusinessTrip(@RequestParam(name = "businessTripId") Long businessTripId,
                                          @RequestParam(name = "iin") String iin) {

        Users user = this.getUserData();

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        TemporaryBusinessTripUsers temp = businessService.getTemporaryBusinessTripUserByIIN(iin);

        if (temp != null) {
            BusinessTripUserDatas userData = new BusinessTripUserDatas();
            userData.setFioData(temp.getFioData());
            userData.setHasDebts(temp.isHasDebts());
            userData.setIin(temp.getIin());
            userData.setPosition(temp.getPosition());
            userData.setAbroadable(temp.isAbroadable());
            userData.setGosServant(temp.isGosServant());
            userData.setBusinessTrip(businessTrip);

            businessService.createBusinessTripUserData(userData);
            businessService.removeTempUserData(temp.getIin());
        }

        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTrip.getId();

    }

    @PostMapping(value = "/addcitytobusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String addCityToBusinessTrip(@RequestParam(name = "businessTripId") Long businessTripId,
                                        @RequestParam(name = "cityId") Long cityId) {

        Users user = this.getUserData();

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);

        if (businessTrip != null) {
            BusinessTripCities city = businessService.getBusinessTripCityById(cityId);
            Set<BusinessTripCities> cities = businessTrip.getBusinessTripCities();
            if (cities == null) {
                cities = new HashSet<>();
            }
            cities.add(city);
        }

        businessService.saveBusinessTrip(businessTrip);

        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTripId;

    }

    @PostMapping(value = "/addroutetobusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String addRouteToBusinessTrip(
            @RequestParam(name = "businessTripId") Long businessTripId,
            @RequestParam(name = "routeId") Long routeId,
            @RequestParam(name = "routeDateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date routeDateFrom,
            @RequestParam(name = "routeDateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date routeDateTo,
            @RequestParam(name = "routeTimeFrom") String routeTimeFrom,
            @RequestParam(name = "routeTimeTo") String routeTimeTo
    ) {

        Users user = this.getUserData();

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);

        if (businessTrip != null && routeDateFrom.getTime() <= routeDateTo.getTime()) {

            BusinessTripRoutes route = businessService.getBusinessTripRouteById(routeId);

            BusinessTripRouteData businessTripRouteData = new BusinessTripRouteData();
            businessTripRouteData.setBusinessTrip(businessTrip);
            businessTripRouteData.setBusinessTripRoute(route);
            businessTripRouteData.setDateFrom(routeDateFrom);
            businessTripRouteData.setDateTo(routeDateTo);
            businessTripRouteData.setTimeFrom(routeTimeFrom);
            businessTripRouteData.setTimeTo(routeTimeTo);

            long diff = routeDateTo.getTime() - routeDateFrom.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            businessTripRouteData.setDaysAmount((int) diffDays);

            businessService.saveBusinessTripRouteData(businessTripRouteData);

        }

        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTripId;

    }

    @PostMapping(value = "/deleteroutefrombusinesstrip")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    @Transactional
    public String deleteRouteFromBusinessTrip(
            @RequestParam(name = "route_id") Long routeId,
            @RequestParam(name = "business_trip_id") Long businessTripId
    ) {
        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);

        if (businessTrip != null) {
            businessService.removeBusinessTripRouteDataByIdAndBusinessTrip(routeId, businessTrip);
        }
        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTripId;
    }

    @PostMapping(value = "/deletecityfrombusinesstrip")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    @Transactional
    public String deleteCityFromBusinessTrip(
            @RequestParam(name = "city_id") Long cityId,
            @RequestParam(name = "business_trip_id") Long businessTripId
    ) {
        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);

        if (businessTrip != null) {
            businessTrip.getBusinessTripCities().removeIf(c -> c.getId().equals(cityId));
            businessService.saveBusinessTrip(businessTrip);
        }
        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTripId;
    }

    @PostMapping(value = "/saveroutedata")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String saveRouteData(
            @RequestParam(name = "businessTripId") Long businessTripId,
            @RequestParam(name = "routeDataId") Long routeDataId,
            @RequestParam(name = "routeDataDays") int routeDataDays,
            @RequestParam(name = "routeDataDaily") boolean routeDataDaily,
            @RequestParam(name = "routeDataOneDay") boolean routeDataOneDay,
            @RequestParam(name = "routeDataResidence") boolean routeDataResidence,
            @RequestParam(name = "routeDataTransport") boolean routeDataTransport
    ) {

        Users user = this.getUserData();

        BusinessTripRouteData businessTripRouteData = businessService.getBusinessTripRouteData(routeDataId);
        businessTripRouteData.setDaysAmount(routeDataDays);
        businessTripRouteData.setDaily(routeDataDaily);
        businessTripRouteData.setOneDay(routeDataOneDay);
        businessTripRouteData.setResidence(routeDataResidence);
        businessTripRouteData.setTransport(routeDataTransport);
        businessService.saveBusinessTripRouteData(businessTripRouteData);

        return "redirect:/zayavitelgo/completebusinesstrip/" + businessTripId;

    }

    @PostMapping(value = "/savebusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String saveBusinessTrip(
            @RequestParam(name = "businessTripId") Long businessTripId,
            @RequestParam(name = "official_president_delegation") Boolean officialPresidentDelegation,
            @RequestParam(name = "inviter_expense") Boolean inviterExpense,
            @RequestParam(name = "visa") Boolean visa,
            @RequestParam(name = "trip_character") String tripCharacter,
            @RequestParam(name = "trip_proof") String tripProof,
            @RequestParam(name = "trip_term_proof") String tripTermProof,
            @RequestParam(name = "passport_type") int passportType,
            @RequestParam(name = "is_foreign_005") Boolean is005,
            @RequestParam(name = "is_event_plan", defaultValue = "false") Boolean isEventPlan,
            @RequestParam(name = "without_event_proof", required = false) String withoutEventProof,
            @RequestParam(name = "plan_process_id", required = false) Long planProcessId,
            @RequestParam(name = "proof_file", required = false) MultipartFile proofFile,
            @RequestParam(name = "ticket_file", required = false) MultipartFile ticketFile
    ) {

        Users user = this.getUserData();

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        if (businessTrip != null
                && user.getId().equals(businessTrip.getCreator().getId())
                && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_REPORTER) {
            businessTrip.setIsOfficialDelegationWithPresident(officialPresidentDelegation);
            businessTrip.setIsAtTheExpenseOfInvitingParty(inviterExpense);
            businessTrip.setIsVisa(visa);
            businessTrip.setTripCharacter(tripCharacter);
            businessTrip.setBusinessTripProof(tripProof);
            businessTrip.setBusinessTripTermProof(tripTermProof);
            businessTrip.setPassportType(passportType);
            businessTrip.setIsForeign005(is005);
            if(ticketFile != null){
                fileUploadService.uploadFile(ticketFile, StaticConfig.FILE_TICKET_TO_BUSINESS_TRIP, businessTrip);
            }
            if (!isEventPlan) {
                if (withoutEventProof != null) {
                    businessTrip.setWithoutEventProof(withoutEventProof);
                    if(!proofFile.isEmpty()){
                        fileUploadService.uploadFile(proofFile, StaticConfig.FILE_WITHOUT_EVENT_PROOF_TO_BUSINESS_TRIP, businessTrip);
                    }
                }
            } else {
                MainProcess process = processService.getProcessById(planProcessId);
                if (process != null)
                    businessTrip.setProcess(process);
            }
            businessTrip.setIsEventPlan(isEventPlan);
            businessService.saveBusinessTrip(businessTrip);
        }
        return "redirect:/zayavitelgo/readbusinesstrip/" + businessTripId;
    }

    @PostMapping(value = "/savepassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String savePassportRequest(
            @RequestParam(name = "passport_request_id") Long passportRequestId,
            @RequestParam(name = "photo3x4_file", required = false) MultipartFile photo3x4File,
            @RequestParam(name = "uds_file", required = false) MultipartFile udsFile
    ) {

        Users user = this.getUserData();

        PassportRequest passportRequest = businessService.getPassportRequestById(passportRequestId);
        if (passportRequest != null
                && user.getId().equals(passportRequest.getBusinessTrip().getCreator().getId())
                && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_REPORTER) {
            if(photo3x4File != null){
                fileUploadService.uploadFile(photo3x4File, StaticConfig.FILE_PHOTO3X4_TO_PASSPORT_REQUEST, passportRequest);
            }
            if(udsFile != null){
                fileUploadService.uploadFile(udsFile, StaticConfig.FILE_UDS_TO_PASSPORT_REQUEST, passportRequest);
            }
            businessService.savePassportRequest(passportRequest);
        }
        return "redirect:/zayavitelgo/readpassportrequest/" + passportRequestId;
    }

    @PostMapping(value = "/savepurposereaching")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String savePurposeReaching(
            @RequestParam(name = "purpose_id") Long purposeId,
            @RequestParam(name = "advance_account_id") Long advanceAccountId,
            @RequestParam(name = "purpose_reaching") String purposeReaching
    ) {

        Users user = this.getUserData();

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(advanceAccountId);
        if (advanceAccount != null
                && user.getId().equals(advanceAccount.getRequester().getId())
                && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER) {
            BusinessTripPurpose purpose = businessService.getBusinessTripPurposeById(purposeId);
            if(advanceAccount.getBusinessTrip().getBusinessTripPurposes().stream().anyMatch(p -> p.getId().equals(purpose.getId()))){
                purpose.setReaching(purposeReaching);
                businessService.saveBusinessTripPurpose(purpose);
            }
        }
        return "redirect:/zayavitelgo/completeadvanceaccount/" + advanceAccountId;
    }


    @PostMapping(value = "/saveadvanceaccount")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String saveAdvanceAccount(
            @RequestParam(name = "advance_account_id") Long advanceAccountId,
            @RequestParam(name = "boarding_pass_file", required = false) MultipartFile boardingPassFile,
            @RequestParam(name = "arrival_departure_file", required = false) MultipartFile arrivalDepartureFile,
            @RequestParam(name = "bank_discharge_file", required = false) MultipartFile bankDischargeFile,
            @RequestParam(name = "bank_receipt_file", required = false) MultipartFile bankReceiptFile,
            @RequestParam(name = "bank_fiscal_file", required = false) MultipartFile bankFiscalFile,
            @RequestParam(name = "card_number", required = false) String cardNumber,
            @RequestParam(name = "card_fio", required = false) String cardFio ,
            @RequestParam(name = "card_expiration", required = false) String cardExpiration,
            @RequestParam(name = "dialog_window", required = false) String dialogWindow
    ) {

        Users user = this.getUserData();

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(advanceAccountId);
        if (advanceAccount != null
                && user.getId().equals(advanceAccount.getBusinessTrip().getCreator().getId())
                && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_REPORTER) {
            advanceAccount.setCardNumber(cardNumber);
            advanceAccount.setCardFio(cardFio);
            advanceAccount.setCardExpiration(cardExpiration);
            advanceAccount.setDialogWindow(dialogWindow);
            if(boardingPassFile != null){
                fileUploadService.uploadFile(boardingPassFile, StaticConfig.FILE_BOARDING_PASS_TO_ADVANCE_ACCOUNT, advanceAccount);
            }
            if(arrivalDepartureFile != null){
                fileUploadService.uploadFile(arrivalDepartureFile, StaticConfig.FILE_ARRIVAL_DEPARTURE_TO_ADVANCE_ACCOUNT, advanceAccount);
            }
            if(bankDischargeFile != null){
                fileUploadService.uploadFile(bankDischargeFile, StaticConfig.FILE_BANK_DISCHARGE_TO_ADVANCE_ACCOUNT, advanceAccount);
            }
            if(bankReceiptFile != null){
                fileUploadService.uploadFile(bankReceiptFile, StaticConfig.FILE_BANK_RECEIPT_TO_ADVANCE_ACCOUNT, advanceAccount);
            }
            if(bankFiscalFile != null){
                fileUploadService.uploadFile(bankFiscalFile, StaticConfig.FILE_BANK_FISCAL_TO_ADVANCE_ACCOUNT, advanceAccount);
            }
            businessService.saveAdvanceAccount(advanceAccount);
        }
        return "redirect:/zayavitelgo/readadvanceaccount/" + advanceAccountId;
    }

    @GetMapping(value = "/readbusinesstrip/{businessTripId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String readBusinessTrip(Model model, @PathVariable(name = "businessTripId") Long businessTripId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        model.addAttribute("businessTrip", businessTrip);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(businessTripId);
        model.addAttribute("userDataList", userDataList);

        List<BusinessTripCities> businessTripCities = businessService.getAllBusinessTripCities();
        model.addAttribute("businessTripCities", businessTripCities);

        List<BusinessTripRoutes> businessTripRoutes = businessService.getAllBusinessTripRoutes();
        model.addAttribute("businessTripRoutes", businessTripRoutes);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(businessTripId);
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<MainProcess> mainProcesses = processService.getProcessesByStatus(StaticConfig.STATUS_ACCEPTED);
        model.addAttribute("mainProcesses", mainProcesses);

        PassportRequest passportRequest = businessService.getPassportRequestByBusinessTrip(businessTrip);
        model.addAttribute("passportRequest", passportRequest);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountByBusinessTrip(businessTrip);
        model.addAttribute("advanceAccount", advanceAccount);

        return "user/zayavitelgo/readbusinesstrip";
    }

    @GetMapping(value = "/readpassportrequest/{passportRequestId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String readPassportRequest(Model model, @PathVariable(name = "passportRequestId") Long passportRequestId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        PassportRequest passportRequest = businessService.getPassportRequestById(passportRequestId);
        model.addAttribute("passportRequest", passportRequest);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(passportRequest.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(passportRequest.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        return "user/zayavitelgo/readpassportrequest";
    }

    @GetMapping(value = "/readadvanceaccount/{advanceAccountId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String readAdvancedAccount(Model model, @PathVariable(name = "advanceAccountId") Long advanceAccountId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(advanceAccountId);
        model.addAttribute("advanceAccount", advanceAccount);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        return "user/zayavitelgo/readadvanceaccount";
    }

    @GetMapping(value = "/completebusinesstrip/{businessTripId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String completeBusinessTrip(Model model,
                                       RedirectAttributes redirectAttributes
            , @PathVariable(name = "businessTripId") Long businessTripId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        model.addAttribute("businessTrip", businessTrip);

        if (businessTrip.getStage() != StaticConfig.BUSINESS_TRIP_REPORTER) {
            redirectAttributes.addFlashAttribute("error", "SIGNED");
            return "redirect:/zayavitelgo/readbusinesstrip/" + businessTrip.getId();
        }

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(businessTripId);
        model.addAttribute("userDataList", userDataList);

        List<BusinessTripCities> businessTripCities = businessService.getAllBusinessTripCities();
        model.addAttribute("businessTripCities", businessTripCities);

        List<BusinessTripRoutes> businessTripRoutes = businessService.getAllBusinessTripRoutes();
        model.addAttribute("businessTripRoutes", businessTripRoutes);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(businessTripId);
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<Users> potentialExecutors =
                userService.getPoliticalExecutorsByOrganizationAndGroup(user.getDepartment().getOrganization(),
                        userService.getGroupByName("Исполнитель Политического департамента МИД РК"));
        model.addAttribute("potentialExecutors", potentialExecutors);
        List<MainProcess> mainProcesses = processService.getProcessesByStatus(StaticConfig.STATUS_ACCEPTED);
        model.addAttribute("mainProcesses", mainProcesses);

        return "user/zayavitelgo/completebusinesstrip";
    }

    @GetMapping(value = "/completepassportrequest/{passportRequestId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String completePassportRequest(Model model,
                                       RedirectAttributes redirectAttributes
            , @PathVariable(name = "passportRequestId") Long passportRequestId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        PassportRequest passportRequest = businessService.getPassportRequestById(passportRequestId);
        model.addAttribute("passportRequest", passportRequest);

        if (passportRequest.getStage() != StaticConfig.PASSPORT_REQUEST_REPORTER) {
            redirectAttributes.addFlashAttribute("error", "SIGNED");
            return "redirect:/zayavitelgo/readpassportrequest/" + passportRequest.getId();
        }

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(passportRequest.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(passportRequest.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<MainProcess> mainProcesses = processService.getProcessesByStatus(StaticConfig.STATUS_ACCEPTED);
        model.addAttribute("mainProcesses", mainProcesses);

        return "user/zayavitelgo/completepassportrequest";
    }

    @GetMapping(value = "/completeadvanceaccount/{advanceAccountId}")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String completeAdvanceAccount(Model model,
                                       RedirectAttributes redirectAttributes
            , @PathVariable(name = "advanceAccountId") Long advanceAccountId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(advanceAccountId);
        model.addAttribute("advanceAccount", advanceAccount);

        if (advanceAccount.getStage() != StaticConfig.ADVANCE_ACCOUNT_REPORTER) {
            redirectAttributes.addFlashAttribute("error", "SIGNED");
            return "redirect:/zayavitelgo/readadvanceaccount/" + advanceAccount.getId();
        }

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<MainProcess> mainProcesses = processService.getProcessesByStatus(StaticConfig.STATUS_ACCEPTED);
        model.addAttribute("mainProcesses", mainProcesses);

        return "user/zayavitelgo/completeadvanceaccount";
    }

    @GetMapping(value = "/businesstrips")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String businessTrips(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {

        Users user = this.getUserData();

        int pageCount = processService.countProcessByDepartment(user.getDepartment());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if (pageCountFinal == 0) {
            pageCountFinal = 1;
        }

        List<BusinessTrips> businessTrips = businessService.getBusinessTripsByCreator(user);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("businessTrips", businessTrips);

        return "user/zayavitelgo/businesstrips";
    }

    @GetMapping(value = "/passports")
    @PreAuthorize("hasAnyRole('ROLE_ZAYAVITEL')")
    public String passports(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {

        Users user = this.getUserData();

        int pageCount = processService.countProcessByDepartment(user.getDepartment());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if (pageCountFinal == 0) {
            pageCountFinal = 1;
        }

        List<PassportRequest> passportRequests = businessService.getPassportRequestsByRequester(user);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("passportRequests", passportRequests);

        return "user/zayavitelgo/passportrequests";
    }

}
