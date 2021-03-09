package kz.bitlab.robygroup.sppmid.core.controllers.user;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.processes.MainProcess;
import kz.bitlab.robygroup.sppmid.core.models.processes.SectionData;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/supervisor")
public class SupervisorMIDController extends BaseController {

    @Autowired
    MainProcessService processService;

    @GetMapping(value = "/processlist")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String processList(Model model, @RequestParam(name = "page", defaultValue = "1") int page){
        Users user = this.getUserData();

        int pageCount = processService.countProcessByOrganization(user.getDepartment().getOrganization());
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if(pageCountFinal==0){
            pageCountFinal = 1;
        }

        List<MainProcess> processList = processService.getAllPagingProcessByOrganization(user.getDepartment().getOrganization(), page, StaticConfig.pageSize);

        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("user", user);
        model.addAttribute("processList", processList);
        return "user/supervisor/processlist";
    }

    @GetMapping(value = "/processdetails/{id}")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String readProcess(Model model, @CookieValue(name = "currencyId", defaultValue = "1") Long currencyId, @PathVariable(name = "id") Long id){

        Users user = this.getUserData();
        model.addAttribute("user", user);
        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());

        List<Currencies> currencies = processService.getAllCurrencies();
        model.addAttribute("currencies", currencies);

        if(process.getStage()==StaticConfig.FIRST_STAGE&&process.getStatus()==StaticConfig.STATUS_SIGNED){

            process.setStage(StaticConfig.SECOND_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SECOND_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SECOND_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        }else if (process.getStage() == StaticConfig.NINTH_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {

            process.setStage(StaticConfig.TENTH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TENTH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TENTH_STAGE));
            process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
            process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));
            process = processService.saveProcess(process);

        }else if (process.getStage() == StaticConfig.ELEVENTH_STAGE && process.getStatus() == StaticConfig.STATUS_SIGNED) {

            process.setStage(StaticConfig.TWELVETH_STAGE);
            process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TWELVETH_STAGE));
            process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TWELVETH_STAGE));
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

        model.addAttribute("currency", currency);
        model.addAttribute("limitExceeded", limitExceeded);
        model.addAttribute("process", process);

        List<Users> ispolnitels = userService.getIspolnitelsByDepartment(user.getDepartment());
        model.addAttribute("ispolnitels", ispolnitels);

        return "user/supervisor/processdetails";
    }

    @PostMapping(value = "/executesign")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String executeSign(
            HttpServletRequest request,
            @RequestParam(name = "processId") Long id,
            @RequestParam(name = "action") String action,
            @RequestParam(name = "comment", required = false, defaultValue = "") String comment){

        Users user = this.getUserData();

        MainProcess process = processService.getProcess(id, user.getDepartment().getOrganization());

        if(process.getStage()==StaticConfig.SECOND_STAGE&&process.getStatus()==StaticConfig.STATUS_UNDER_CONSIDERATION){

            if(action.equals("sign")){
                process.setStage(StaticConfig.SECOND_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.SECOND_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.SECOND_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            }else if(action.equals("modify")){

                process.setStage(StaticConfig.FIRST_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.FIRST_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.FIRST_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setIsDeclined(1);
                process.setComment(comment);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);

        }else if(process.getStage()==StaticConfig.TENTH_STAGE&&process.getStatus()==StaticConfig.STATUS_UNDER_CONSIDERATION){

            if(action.equals("sign")){

                process.setStage(StaticConfig.TENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

                List<Users> ispolnitels = userService.getIspolnitelsByDepartment(user.getDepartment());

                Set<Users> performers = new HashSet<>();

                for(Users isp : ispolnitels){
                    if(request.getParameter("ispolnitel_"+isp.getId())!=null) {
                        performers.add(isp);
                    }
                }

                process.setMidPerformers(performers);

            }else if(action.equals("modify")){

                process.setStage(StaticConfig.NINTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.NINTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.NINTH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setIsDeclined(1);
                process.setComment(comment);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);

        }else if(process.getStage()==StaticConfig.TWELVETH_STAGE&&process.getStatus()==StaticConfig.STATUS_UNDER_CONSIDERATION){

            if(action.equals("sign")){

                process.setStage(StaticConfig.TWELVETH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.TWELVETH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.TWELVETH_STAGE));
                process.setStatus(StaticConfig.STATUS_SIGNED);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_SIGNED));
                process.setIsDeclined(0);
                process.setComment("");

            }else if(action.equals("modify")){

                process.setStage(StaticConfig.ELEVENTH_STAGE);
                process.setStageName(StaticConfig.processStageNames.get(StaticConfig.ELEVENTH_STAGE));
                process.setStageRole(StaticConfig.processStageRoles.get(StaticConfig.ELEVENTH_STAGE));
                process.setStatus(StaticConfig.STATUS_UNDER_CONSIDERATION);
                process.setIsDeclined(1);
                process.setComment(comment);
                process.setStatusText(StaticConfig.statusText.get(StaticConfig.STATUS_UNDER_CONSIDERATION));

            }
            process.setIsUncombinable(0);
            processService.saveProcess(process);
        }
        return "redirect:/supervisor/processdetails/"+id+"?success";
    }

    @PostMapping(value = "/setcurrency")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String addSectionData(Model model, HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(name = "currency_id") Long currencyId,
                                 @RequestParam(name = "process_id") Long processId
    ) {

        Currencies currency = processService.getCurrency(currencyId);
        if(currency!=null) {
            Cookie cookie = new Cookie("currencyId", currency.getId()+"");
            cookie.setMaxAge(30*24*3600);
            response.addCookie(cookie);
        }
        return "redirect:/supervisor/processdetails/"+processId;
    }

    @PostMapping(value = "/combineprocess")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String combineProcesses(HttpServletRequest request) {

        Users user = this.getUserData();

        List<MainProcess> processes = processService.getProcessListByOrganization(user.getDepartment().getOrganization());
        List<MainProcess> processesToCombine = new ArrayList<>();

        for(MainProcess proc : processes){
            if(request.getParameter("process_"+proc.getId())!=null){
                processesToCombine.add(proc);
            }
        }

        processService.combineProcess(processesToCombine);
        return "redirect:/supervisor/processlist";
    }

    @PostMapping(value = "/uncombineprocess")
    @PreAuthorize("hasAnyRole('ROLE_RUKOVODITEL_VFD')")
    public String unCombineProcesses(@RequestParam(name = "uncombine_process_id") Long processId) {

        Users user = this.getUserData();
        MainProcess process = processService.getProcess(processId, user.getDepartment().getOrganization());
        if(process!=null){
            List<MainProcess> subProcesses = processService.getProcessesByParentId(process.getId());
            processService.unCombineProcess(subProcesses, process);
        }

        return "redirect:/supervisor/processlist";
    }
}
