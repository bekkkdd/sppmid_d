package kz.bitlab.robygroup.sppmid.core.controllers.user;


import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripRouteData;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripUserDatas;
import kz.bitlab.robygroup.sppmid.core.models.processes.AdvanceAccount;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
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

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/executorvfd")
public class ExecutorVfdController extends BaseController {

    @Autowired
    MainProcessService processService;

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/readadvanceaccount/{advanceAccountId}")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_VFD')")
    public String readAdvanceAccount(Model model, @PathVariable(name = "advanceAccountId") Long advanceAccountId) {

        Users user = this.getUserData();
        model.addAttribute("user", user);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(advanceAccountId);
        model.addAttribute("advanceAccount", advanceAccount);

        if(advanceAccount.getStage() < StaticConfig.ADVANCE_ACCOUNT_VFD_EXECUTOR){
            return "redirect:/executorvfd/advanceaccounts";
        }

        List<BusinessTripRouteData> businessTripRouteDataList = businessService.getBusinessTripRouteDataList(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("businessTripRouteDataList", businessTripRouteDataList);

        List<BusinessTripUserDatas> userDataList = businessService.getBusinessTripUserDatas(advanceAccount.getBusinessTrip().getId());
        model.addAttribute("userDataList", userDataList);

        return "user/executorvfd/readadvanceaccount";
    }

    @PostMapping(value = "/signadvanceaccount")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_VFD')")
    public String signAdvanceAccount(Model model,
                                      @RequestParam(name = "advance_account_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (advanceAccount != null
                && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_VFD_EXECUTOR) {
            advanceAccount.setStage(StaticConfig.ADVANCE_ACCOUNT_VFD_SUPERVISOR);
            businessService.saveAdvanceAccount(advanceAccount);
        }
        return "redirect:/executorvfd/readadvanceaccount/" + advanceAccount.getId();
    }

    @PostMapping(value = "/modifyadvanceaccount")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_VFD')")
    public String modifyAdvanceAccount(Model model,
                                        @RequestParam(name = "advance_account_id") Long id) {

        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);

        AdvanceAccount advanceAccount = businessService.getAdvanceAccountById(id);
        if (advanceAccount != null && advanceAccount.getStage() == StaticConfig.ADVANCE_ACCOUNT_VFD_EXECUTOR) {
            advanceAccount.setStage(StaticConfig.ADVANCE_ACCOUNT_REPORTER);
            businessService.saveAdvanceAccount(advanceAccount);
        }
        return "redirect:/executorvfd/advanceaccounts";
    }

    @GetMapping(value = "/advanceaccounts")
    @PreAuthorize("hasAnyRole('ROLE_ISPOLNITEL_VFD')")
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

        model.addAttribute("user", user);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("user", user);
        model.addAttribute("advanceAccounts", businessService.getAdvanceAccountsByStageGreaterThanEqual(StaticConfig.ADVANCE_ACCOUNT_VFD_EXECUTOR));

        return "user/executorvfd/advanceaccounts";
    }
}
