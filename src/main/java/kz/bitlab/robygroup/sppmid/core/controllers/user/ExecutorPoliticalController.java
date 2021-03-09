package kz.bitlab.robygroup.sppmid.core.controllers.user;


import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import kz.bitlab.robygroup.sppmid.core.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/executorpolitical")
public class ExecutorPoliticalController extends BaseController {

    @Autowired
    MainProcessService processService;

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/processlist")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
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

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("processList", processList);

        return "user/executorpolitical/processlist";
    }

    @GetMapping(value = "/processdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String readProcess(Model model, @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId, @PathVariable(name = "id") Long id) {

        Users user = this.getUserData();

        model.addAttribute("user", user);
        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());

        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        if (process.getStage() == StaticConfig.SECOND_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {

            process.setStage(StaticConfig.THIRD_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.THIRD_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.THIRD_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        } else if (process.getStage() == StaticConfig.EIGHTH_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {

            process.setStage(StaticConfig.NINTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.NINTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.NINTH_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        } else if (process.getStage() == StaticConfig.TWELVETH_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {

            process.setStage(StaticConfig.THIRTEENTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.THIRTEENTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.THIRTEENTH_STAGE));
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
        return "user/executorpolitical/processdetails";
    }


    @GetMapping(value = "/businesstrips")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
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

        List<BusinessTrips> businessTrips = businessService
                .getBusinessTripsByStageGreaterThanEqualAndExecutor(StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR,
                        user);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("businessTrips", businessTrips);

        return "user/executorpolitical/businesstrips";
    }

    @GetMapping(value = "/readbusinesstrip/{businessTripId}")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String readBusinessTrip(Model model, RedirectAttributes redirectAttributes, @PathVariable(name = "businessTripId") Long businessTripId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        BusinessTrips businessTrip = businessService.getBusinessTripById(businessTripId);
        if (businessTrip == null || businessTrip.getStage() < StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR
                || businessTrip.getExecutor() == null
                || !businessTrip.getExecutor().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "SIGNED");
            return "redirect:/executorpolitical/businesstrips";
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

        return "user/executorpolitical/readbusinesstrip";
    }

    @PostMapping(value = "/savepurposetobusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String savePurposeToBusinessTrip(Model model,
                                            @RequestParam(name = "business_trip_id") Long id,
                                            @RequestParam(name = "trip_purpose") String purpose) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR && !StringUtils.isEmpty(purpose)) {
            businessTrip.getBusinessTripPurposes().add(businessService.saveBusinessTripPurpose(new BusinessTripPurpose(businessTrip, purpose, null)));
            businessService.saveBusinessTrip(businessTrip);
        }
        return "redirect:/executorpolitical/readbusinesstrip/" + businessTrip.getId();
    }

    @PostMapping(value = "/deletepurposefrombusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String deletePurposeFromBusinessTrip(Model model,
                                            @RequestParam(name = "business_trip_id") Long id,
                                            @RequestParam(name = "purpose_id") Long purpose_id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if(businessTrip != null && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR
                && businessTrip.getBusinessTripPurposes().stream().anyMatch(p -> p.getId().equals(purpose_id))){
            businessService.deleteBusinessTripPurposeById(purpose_id);
        }
        return "redirect:/executorpolitical/readbusinesstrip/" + businessTrip.getId();
    }

    @PostMapping(value = "/signbusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String signBusinessTrip(Model model,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam(name = "business_trip_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null
                &&
                (businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR
                    && businessTrip.getBusinessTripPurposes() != null && businessTrip.getBusinessTripPurposes().size() >= 5
                || (businessTrip.getAttachment6Created() != null
                && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_MID_SECRETARY_SIGNED))) {
            businessTrip.setStage(StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR_SUPERVISOR);
            businessService.saveBusinessTrip(businessTrip);
            return "redirect:/executorpolitical/readbusinesstrip/" + businessTrip.getId();
        } else if (businessTrip != null && businessTrip.getAttachment6Created() == null
                && businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_MID_SECRETARY_SIGNED) {
            businessTrip.setAttachment6Created(new Date());
            businessService.saveBusinessTrip(businessTrip);
            return "redirect:/executorpolitical/readbusinesstrip/" + businessTrip.getId();
        } else if (businessTrip != null && (businessTrip.getBusinessTripPurposes() == null || businessTrip.getBusinessTripPurposes().size() < 5)) {
            redirectAttributes.addFlashAttribute("error", "Недостаточно целей поездки!");
            return "redirect:/executorpolitical/readbusinesstrip/" + businessTrip.getId();
        }
        return "redirect:/executorpolitical/businesstrips";
    }

    @PostMapping(value = "/modifybusinesstrip")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String modifyBusinessTrip(Model model,
                                     @RequestParam(name = "business_trip_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        BusinessTrips businessTrip = businessService.getBusinessTripById(id);
        if (businessTrip != null &&
                (businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_MID_SECRETARY_SIGNED
                        || businessTrip.getStage() == StaticConfig.BUSINESS_TRIP_POLITICAL_EXECUTOR)) {
            businessService.refactorFieldsToModify(businessTrip);
            businessService.saveBusinessTrip(businessTrip);
        }
        return "redirect:/executorpolitical/businesstrips";
    }

    @PostMapping(value = "/executesign")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String executeSign(Model model,
                              @RequestParam(name = "processId") Long id,
                              @RequestParam(name = "action") String action,
                              @RequestParam(name = "comment", required = false, defaultValue = "") String comment) {

        Users user = this.getUserData();
        model.addAttribute("user", user);
        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());

        if (process.getStage() == StaticConfig.THIRD_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {

            if (action.equals("sign")) {

                process.setStage(StaticConfig.THIRD_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.THIRD_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.THIRD_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            } else if (action.equals("modify")) {

                process.setStage(StaticConfig.SECOND_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SECOND_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SECOND_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);

        } else if (process.getStage() == StaticConfig.NINTH_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {

            if (action.equals("sign")) {

                process.setStage(StaticConfig.NINTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.NINTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.NINTH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            } else if (action.equals("modify")) {

                process.setStage(StaticConfig.EIGHTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.EIGHTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.EIGHTH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);

        } else if (process.getStage() == StaticConfig.THIRTEENTH_STAGE && process.getStatus() == StaticConfig.STATUS_UNDER_CONSIDERATION) {

            if (action.equals("sign")) {

                process.setStage(StaticConfig.THIRTEENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.THIRTEENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.THIRTEENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_ACCEPTED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_ACCEPTED));
                process.setIsDeclined(0);
                process.setComment("");

            } else if (action.equals("modify")) {

                process.setStage(StaticConfig.TWELVETH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TWELVETH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TWELVETH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
                process.setIsDeclined(1);
                process.setComment(comment);

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);
        }
        return "redirect:/executorpolitical/processdetails/" + id + "?success";
    }


    @PostMapping(value = "/setcurrency")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
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
        return "redirect:/executorpolitical/processdetails/" + processId;
    }

    @PostMapping(value = "/combineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
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
        return "redirect:/executorpolitical/processlist";
    }

    @PostMapping(value = "/uncombineprocess")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_POLIT_DEP_MID')")
    public String unCombineProcesses(@RequestParam(name = "uncombine_process_id") Long processId) {

        Users user = this.getUserData();
        MainProcess process = processService.getProcess(processId, user.getDepartment().getOrganization());
        if (process != null) {
            List<MainProcess> subProcesses = processService.getProcessesByParentId(process.getId());
            processService.unCombineProcess(subProcesses, process);
        }

        return "redirect:/executorpolitical/processlist";
    }
}
