package kz.bitlab.robygroup.sppmid.core.controllers.user;


import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.*;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.BusinessService;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
@RequestMapping(value = "/executordks")
public class ExecutorDksController extends BaseController {

    @Autowired
    MainProcessService processService;

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/passports")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_DKS_MID')")
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

        List<PassportRequest> passportRequests = businessService
                .getPassportRequestsByStageGreaterThanEqualAndExecutor(StaticConfig.PASSPORT_REQUEST_DKS_EXECUTOR,
                        user);

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("passportRequests", passportRequests);

        return "user/executordks/passportrequests";
    }

    @GetMapping(value = "/readpassportrequest/{passportRequestId}")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_DKS_MID')")
    public String readPassportRequest(Model model, @PathVariable(name = "passportRequestId") Long passportRequestId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        PassportRequest passportRequest = businessService.getPassportRequestById(passportRequestId);
        model.addAttribute("passportRequest", passportRequest);

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(passportRequest.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(passportRequest.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        return "user/executordks/readpassportrequest";
    }

    @PostMapping(value = "/signpassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_DKS_MID')")
    public String signPassportRequest(Model model,
                                      @RequestParam(name = "passport_request_id") Long id,
                                      @RequestParam(name = "comment") String comment
                                      ) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (passportRequest != null
                && passportRequest.getExecutor() != null
                && !StringUtils.isEmpty(comment)
                && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_DKS_EXECUTOR) {
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_DKS_EXECUTOR_READY);
            passportRequest.setCommentToRequester(comment);
            businessService.savePassportRequest(passportRequest);
        }
        return "redirect:/executordks/readpassportrequest/" + passportRequest.getId();
    }

    @PostMapping(value = "/modifypassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_DKS_MID')")
    public String modifyPassportRequest(Model model,
                                        @RequestParam(name = "passport_request_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        if (passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_DKS_SUPERVISOR) {
            businessService.refactorFieldsToModifyForPassportRequest(passportRequest, "Comment for modify");
            businessService.savePassportRequest(passportRequest);
        }
        return "redirect:/executordks/passports";
    }

    @PostMapping(value = "/deletepassportrequest")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_DKS_MID')")
    public String deletePassportRequest(Model model,
                                        @RequestParam(name = "passport_request_id") Long id,
                                        @RequestParam(name = "delete_reason") Integer deleteReason) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        PassportRequest passportRequest = businessService.getPassportRequestById(id);
        String deleteReasonStr = StaticConfig.passportRequestDeletes.get(deleteReason);
        if (passportRequest != null && passportRequest.getStage() == StaticConfig.PASSPORT_REQUEST_REPORTER_TOOK
                && deleteReasonStr != null) {
            passportRequest.setDeleteReason(deleteReasonStr);
            passportRequest.setStage(StaticConfig.PASSPORT_REQUEST_DKS_EXECUTOR_DELETED);
            businessService.savePassportRequest(passportRequest);
        }
        return "redirect:/executordks/passports";
    }
}
