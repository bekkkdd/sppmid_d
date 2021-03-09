package kz.bitlab.robygroup.sppmid.core.controllers.user;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/secretarygo")
public class SecretaryGOController extends BaseController {

    @Autowired
    MainProcessService processService;

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/processlist")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String processList(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        Users user = this.getUserData();

        int pageCount = processService.countProcessByGosOrganization(user.getDepartment().getOrganization());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if(pageCountFinal==0){
            pageCountFinal = 1;
        }

        List<MainProcess> processList = processService.getProcessListByGosOrganization(user.getDepartment().getOrganization(), page, StaticConfig.pageSize);
        List<Currencies> currencies = processService.getAllCurrencies();

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("processList", processList);
        model.addAttribute("currencies", currencies);

        return "user/secretarygo/processlist";

    }

    @GetMapping(value = "/processdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String readProcess(Model model, @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId, @PathVariable(name = "id") Long id){

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(id, currentUser.getDepartment().getOrganization());

        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        if(process.getStage() == StaticConfig.THIRD_STAGE&&process.getStatus()==StaticConfig.STATUS_SIGNED){

            process.setStage(StaticConfig.FOURTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.FOURTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.FOURTH_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        }else if(process.getStage()==StaticConfig.SEVENTH_STAGE&&process.getStatus()==StaticConfig.STATUS_SIGNED){

            process.setStage(StaticConfig.EIGHTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.EIGHTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.EIGHTH_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        }

        Currencies currency = processService.getCurrency(currencyId);

        if(currency==null){
            currency = processService.getCurrency(1L); // GET TENGE
        }

        int totalExpence = 0;

        if(!process.getSectionData().isEmpty()){
            for(SectionData sectionData : process.getSectionData()){
                if(sectionData.getCurrency().getId()!=currency.getId()) {
                    double coeff = sectionData.getCurrency().getTengeBuyRatio() / currency.getTengeSellRatio();
                    sectionData.setDailyExpences((int) (sectionData.getDailyExpences() * coeff));
                    sectionData.setFareExpences((int) (sectionData.getFareExpences() * coeff));
                    sectionData.setResidenceExpences((int) (sectionData.getResidenceExpences() * coeff));
                }
                totalExpence = totalExpence + sectionData.getDailyExpences() + sectionData.getFareExpences() + sectionData.getResidenceExpences();
            }
        }

        if(process.getCurrency().getId()!=currency.getId()){
            process.setLimitValue((int)(process.getLimitValue() * process.getCurrency().getTengeBuyRatio() / currency.getTengeSellRatio()));
        }

        boolean limitExceeded = totalExpence>process.getLimitValue();
        List<Departments> departments = userService.getAllDepartmentsByOrganization(process.getGosOrganization());

        model.addAttribute("departments", departments);
        model.addAttribute("currency", currency);
        model.addAttribute("process", process);
        model.addAttribute("limitExceeded", limitExceeded);

        return "user/secretarygo/processdetails";
    }


    @GetMapping(value = "/businesstrips")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
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

        List<BusinessTrips> businessTrips = businessService.getBusinessTripsByStageGreaterThanEqual(StaticConfig.BUSINESS_TRIP_GO_SECRETARY);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("businessTrips", businessTrips);

        return "user/secretarygo/businesstrips";
    }

    @GetMapping(value = "/readbusinesstrip/{businessTripId}")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String readBusinessTrip(Model model, RedirectAttributes redirectAttributes, @PathVariable(name = "businessTripId") Long businessTripId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        if(businessTrip == null || businessTrip.getStage() < StaticConfig.BUSINESS_TRIP_GO_SECRETARY)
        {
            redirectAttributes.addFlashAttribute("error", "SIGNED");
            return "redirect:/secretarygo/businesstrips";
        }
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

        return "user/secretarygo/readbusinesstrip";
    }

    @PostMapping(value = "/signbusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String signBusinessTrip(Model model,
                                   @RequestParam(name = "business_trip_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_GO_SECRETARY
            && businessTrip.getFoundsAllocatedAt() == null) {
            businessTrip.setStage(StaticConfig.BUSINESS_TRIP_MID_SECRETARY);
            businessService.saveBusinessTrip(businessTrip);
            return "redirect:/secretarygo/readbusinesstrip/" + businessTrip.getId();
        } else if(businessTrip != null && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_MID_GO_SECRETARY
                && businessTrip.getFoundsAllocatedAt() != null){
            businessTrip.setStage(StaticConfig.BUSINESS_TRIP_ORDERED);
            businessService.saveBusinessTrip(businessTrip);
            return "redirect:/secretarygo/readbusinesstrip/" + businessTrip.getId();
        }
        return "redirect:/secretarygo/businesstrips";
    }

    @PostMapping(value = "/modifybusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String modifyBusinessTrip(Model model,
                                     @RequestParam(name = "business_trip_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null &&
                (businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_GO_SECRETARY
                || businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_MID_GO_SECRETARY)) {
            businessService.refactorFieldsToModify(businessTrip);
            businessService.saveBusinessTrip(businessTrip);
        }
        return "redirect:/secretarygo/businesstrips";
    }

    @PostMapping(value = "/executesign")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String executeSign(Model model,
                              HttpServletRequest request,
                              @RequestParam(name = "processId") Long id,
                              @RequestParam(name = "action") String action,
                              @RequestParam(name = "comment", required = false, defaultValue = "") String comment,
                              @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId){

        Users user = this.getUserData();
        model.addAttribute("user", user);
        MainProcess process = processService.getProcessByGosOrgan(id, user.getDepartment().getOrganization());

        if(process.getStage()==StaticConfig.FOURTH_STAGE&&process.getStatus()==StaticConfig.STATUS_UNDER_CONSIDERATION){
            if(action.equals("sign")){

                List<Departments> departments = userService.getAllDepartmentsByOrganization(process.getGosOrganization());
                List<MainProcess> processes = new ArrayList<>();
                int totalLimit = 0;

                for(Departments dept : departments){
                    if(request.getParameter("department_"+dept.getId())!=null){

                        int limitForEachDept = Integer.parseInt(request.getParameter("limit_"+dept.getId()));

                        Currencies limitCurrency = processService.getCurrency(currencyId);
                        if(limitCurrency==null){
                            limitCurrency = processService.getCurrency(1L); // GET TENGE
                        }

                        if(limitCurrency.getId()==process.getCurrency().getId()){
                            totalLimit+=limitForEachDept;
                        }else{
                            totalLimit+=(limitForEachDept*process.getCurrency().getTengeBuyRatio()/limitCurrency.getTengeSellRatio());
                        }

                        processes.add(
                                new MainProcess(
                                        process.getId(),
                                        process.getOrganization(),
                                        process.getGosOrganization(),
                                        dept,
                                        process.getAuthor(),
                                        limitCurrency,
                                        limitForEachDept,
                                        limitForEachDept,
                                        process.getFirstYear(),
                                        process.getSecondYear(),
                                        process.getThirdYear(),
                                        StaticConfig.STATUS_SIGNED,
                                        StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED),
                                        process.getStage(),
                                        process.getStageRole(),
                                        process.getStageName(),
                                        0,
                                        "",
                                        0,
                                        0,
                                        null,
                                        null,
                                        null
                                )
                        );
                    }
                }

                process.setStage(StaticConfig.FOURTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.FOURTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.FOURTH_STAGE));
                process.setLimitValue(process.getLimitValue()-totalLimit);

                if(process.getLimitValue()>0){
                    process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                    process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                }else{
                    process.setStatus(StaticConfig.STATUS_COMBINED);
                    process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_COMBINED));
                }
                process.setIsDeclined(0);
                process.setComment("");

                processService.executeProcesses(processes);

            }else if(action.equals("modify")){

                process.setStage(StaticConfig.THIRD_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.THIRD_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.THIRD_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);

            }
            processService.saveProcess(process);

        }else if(process.getStage()==StaticConfig.EIGHTH_STAGE&&process.getStatus()==StaticConfig.STATUS_UNDER_CONSIDERATION){

            if(action.equals("sign")){

                process.setStage(StaticConfig.EIGHTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.EIGHTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.EIGHTH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            }else if(action.equals("modify")){

                process.setStage(StaticConfig.SEVENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SEVENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SEVENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);

            }

            process.setIsUncombinable(0);
            processService.saveProcess(process);
        }

        return "redirect:/secretarygo/processlist";
    }

    @PostMapping(value = "/addsectiondata")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
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
        if(sectionDataList==null){
            sectionDataList = new HashSet<>();
        }

        Currencies currency = processService.getCurrency(currencyId);

        SectionData sectionData = processService.addSectionData(new SectionData(type, process.getId(), process.getId(), name, country, limitTimeFrom, limitTimeTo, participantAmount, daysAmount, currency, dailyExpences, residenceExpences, fareExpences));
        sectionDataList.add(sectionData);

        process.setSectionData(sectionDataList);
        processService.saveProcess(process);

        return "redirect:/secretarygo/processdetails/"+id;

    }

    @PostMapping(value = "/removesectiondata")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String addSectionData(Model model,
                                 @RequestParam(name = "processId") Long processId,
                                 @RequestParam(name = "sectionId") Long sectionId
    ) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        MainProcess process = processService.getProcessByGosOrgan(processId, currentUser.getDepartment().getOrganization());

        Set<SectionData> sectionDataList = process.getSectionData();
        if(sectionDataList==null){
            sectionDataList = new HashSet<>();
        }

        for(SectionData sec : sectionDataList){
            if(sec.getId()==sectionId){
                sectionDataList.remove(sec);
                break;
            }
        }

        process.setSectionData(sectionDataList);
        processService.saveProcess(process);

        return "redirect:/secretarygo/processdetails/"+processId;

    }

    @PostMapping(value = "/setcurrency")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String addSectionData(HttpServletResponse response,
                                 @RequestParam(name = "currency_id") Long currencyId,
                                 @RequestParam(name = "process_id") Long processId
    ) {

        Currencies currency = processService.getCurrency(currencyId);
        if(currency!=null) {
            Cookie cookie = new Cookie("currencyId", currency.getId()+"");
            cookie.setMaxAge(30*24*3600);
            response.addCookie(cookie);
        }
        return "redirect:/secretarygo/processdetails/"+processId;
    }

    @PostMapping(value = "/combineprocess")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String combineProcesses(HttpServletRequest request) {

        Users user = this.getUserData();

        List<MainProcess> processes = processService.getProcessListByGosOrganization(user.getDepartment().getOrganization());
        List<MainProcess> processesToCombine = new ArrayList<>();

        for(MainProcess proc : processes){
            if(request.getParameter("process_"+proc.getId())!=null){
                processesToCombine.add(proc);
            }
        }

        processService.combineProcess(processesToCombine);
        return "redirect:/secretarygo/processlist";
    }

    @PostMapping(value = "/uncombineprocess")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String unCombineProcesses(@RequestParam(name = "uncombine_process_id") Long processId) {

        Users user = this.getUserData();
        MainProcess process = processService.getProcessByGosOrgan(processId, user.getDepartment().getOrganization());
        if(process!=null){
            List<MainProcess> subProcesses = processService.getProcessesByParentId(process.getId());
            processService.unCombineProcess(subProcesses, process);
        }

        return "redirect:/secretarygo/processlist";
    }



    @GetMapping(value = "/passports")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
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

        List<PassportRequest> passportRequests = businessService.getPassportRequestsByStageGreaterThanEqual(StaticConfig.PASSPORT_REQUEST_GO_SECRETARY);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("passportRequests", passportRequests);

        return "user/secretarygo/passportrequests";
    }

    @GetMapping(value = "/readpassportrequest/{passportRequestId}")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String readPassportRequest(Model model, @PathVariable(name = "passportRequestId") Long passportRequestId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        PassportRequest passportRequest = businessService.getPassportRequestById(passportRequestId);
        model.addAttribute("passportRequest", passportRequest);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(passportRequest.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(passportRequest.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        return "user/secretarygo/readpassportrequest";
    }

    @PostMapping(value = "/signpassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String signPassportRequest(Model model,
                                      @RequestParam(name = "passport_request_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_GO_SECRETARY) {
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_MID_SECRETARY);
            businessService.savePassportRequest(passportRequest);
            return "redirect:/secretarygo/readpassportrequest/" + passportRequest.getId();
        }
        return "redirect:/secretarygo/passportrequests";

    }

    @PostMapping(value = "/modifypassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_OTVESTVENNIY_SEKRETAR_GO')")
    public String modifyPassportRequest(Model model,
                                        @RequestParam(name = "passport_request_id") Long id) {
        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_GO_SECRETARY) {
            businessService.refactorFieldsToModifyForPassportRequest(passportRequest, "Comment for modify");
            businessService.savePassportRequest(passportRequest);
        }
        return "redirect:/secretarygo/passports";
    }
}
