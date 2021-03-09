package kz.bitlab.robygroup.sppmid.core.controllers.admin;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.services.MailService;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.user.*;
import kz.bitlab.robygroup.sppmid.core.services.MainProcessService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/super")
public class SuperAdminController extends BaseController {

    private PasswordEncoder passwordEncoder;
    private MainProcessService processService;
    private MailService mailService;

    @GetMapping(value = "/index")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String index(Model model){
        this.mailService.sendSimpleMessage("asilkhan-al@mail.ru", "kasya", "vasya");
        model.addAttribute("user", this.getUserData());
        return "superadmin/index";
    }

    @GetMapping(value = "/adduser")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addUser(Model model){
        List<Groups> groups = userService.getAllGroups(true);
        List<Organizations> organizations = userService.getAllOrganizations();

        model.addAttribute("groups", groups);
        model.addAttribute("organizations", organizations);
        model.addAttribute("user", this.getUserData());

        return "super/adduser";
    }

    @PostMapping(value = "/adduser")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addUser(@RequestParam(name = "user_login") String login,
                          @RequestParam(name = "user_password") String password,
                          @RequestParam(name = "re_user_password") String rePassword,
                          @RequestParam(name = "group_id") Long groupId,
                          @RequestParam(name = "department_id") Long departmentId,
                          @RequestParam(name = "email", required = false, defaultValue = "") String email,
                          @RequestParam(name = "firstname", required = false, defaultValue = "") String firstName,
                          @RequestParam(name = "lastname", required = false, defaultValue = "") String lastName,
                          @RequestParam(name = "middlename", required = false, defaultValue = "") String middleName){

        String message = "?error";
        if(userService.findByLogin(login)==null){
            if(password.trim().length()>=6&&password.equals(rePassword)){
                Groups group = userService.getGroup(groupId);
                Departments department = userService.findDeparmentById(departmentId);
                if(department!=null) {
                    Users user = new Users(login, passwordEncoder.encode(password), group, department, firstName, lastName, middleName,email);
                    if (userService.register(user) != null) {
                        message = "?success";
                    }
                }
            }else {
                message = "?errorWithPassword";
            }
        }
        else{
            message = "?errorWithLogin";
        }
        return "redirect:/super/adduser"+message;
    }


    @PostMapping(value = "/addcurrency")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addCurrency(@RequestParam(name = "name") String name,
                              @RequestParam(name = "symbol") String symbol,
                              @RequestParam(name = "tenge_buy_ratio") double tengeBuyRatio,
                              @RequestParam(name = "tenge_cell_ratio") double tengeSellRatio){

        String message = "?error";
        if( userService.addCurrency(new Currencies(name, symbol, tengeBuyRatio, tengeSellRatio))!= null){
            message = "?success";
        }
        return "redirect:/super/listcurrencies"+message;
    }

    @GetMapping(value = "/listusers")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String listUsers(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        Users currentUser = getUserData();
        int pageCount = userService.countUsers();

        if(page<1){
            page = 1;
        }

        List<Users> users = userService.getAllPagingUsers(page-1, StaticConfig.pageSize);
        model.addAttribute("pageCount", (int)(Math.ceil((double)pageCount/StaticConfig.pageSize)));
        model.addAttribute("users", users);
        model.addAttribute("user", currentUser);

        return "super/listusers";
    }

    @GetMapping(value = "/listcurrencies")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String listCurrencies(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        Users currentUser = getUserData();
        int pageCount = processService.countCurrencies();

        if(page<1){
            page = 1;
        }

        List<Currencies> currencies = processService.getAllPagingCurrencies(page-1, StaticConfig.pageSize);
        model.addAttribute("pageCount", (int)(Math.ceil((double)pageCount/StaticConfig.pageSize)));
        model.addAttribute("currencies", currencies);
        model.addAttribute("user", currentUser);

        return "super/listcurrencies";
    }

    @GetMapping(value = "/listgroups")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String listGroups(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        Users currentUser = getUserData();

        int pageCount = userService.countGroups();

        if(page<1){
            page = 1;
        }

        List<Groups> groups = userService.getAllPagingGroups(page-1, StaticConfig.pageSize);

        model.addAttribute("pageCount", (int)(Math.ceil((double)pageCount/StaticConfig.pageSize)));
        model.addAttribute("groups", groups);
        model.addAttribute("user", currentUser);

        return "super/listgroups";
    }

    @GetMapping(value = "/details/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String userDetail(Model model, @PathVariable(name = "id") Long id){

        List<Groups> groups = userService.getAllGroups(true);
        model.addAttribute("groups", groups);
        Users userDetail = userService.findById(id);
        model.addAttribute("userDetail", userDetail);
        List<Organizations> organizations = userService.getAllOrganizations();
        model.addAttribute("organizations", organizations);
        List<Departments> departments = userService.getAllDepartmentsByOrganization(userDetail.getDepartment().getOrganization());
        model.addAttribute("departments", departments);

        model.addAttribute("user", this.getUserData());

        return "super/details";
    }

    @PostMapping(value = "/updateuser")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateUser(@RequestParam(name = "id") Long id,
                             @RequestParam(name = "user_login") String login,
                             @RequestParam(name = "firstname") String firstName,
                             @RequestParam(name = "lastname") String lastName,
                             @RequestParam(name = "email", required = false, defaultValue = "") String email,
                             @RequestParam(name = "middlename") String middleName,
                             @RequestParam(name = "group_id") Long groupId,
                             @RequestParam(name = "department_id") Long departmentId){

        String message = "error";
        Departments department = userService.findDeparmentById(departmentId);
        if(department!=null){
            Users user = userService.findById(id);
            Groups group = userService.getGroup(groupId);
            if(user!=null){
                user.setLogin(login);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setMiddleName(middleName);
                user.setGroup(group);
                user.setEmail(email);
                user.setDepartment(department);
                userService.updateUser(user);
                message = "success";
            }
        }

        return "redirect:/super/details/"+id+"?"+message;
    }

    @PostMapping(value = "/updatecurrency")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateCurrency(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "name") String name,
                                 @RequestParam(name = "symbol") String symbol,
                                 @RequestParam(name = "tenge_buy_ratio") double tengeBuyRatio,
                                 @RequestParam(name = "tenge_sell_ratio") double tengeSellRatio){

        String message = "error";
        Currencies currency = userService.findCurrencyById(id);

        if(currency!=null){
            currency.setName(name);
            currency.setSymbol(symbol);
            currency.setTengeBuyRatio(tengeBuyRatio);
            currency.setTengeSellRatio(tengeSellRatio);
            userService.saveCurrency(currency);
            message = "success";
        }

        return "redirect:/super/currencydetails/"+id+"?"+message;
    }

    @PostMapping(value = "/updatepassword")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updatePassword(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "user_new_password") String newPassword,
                                 @RequestParam(name = "re_user_new_password") String reNewPassword){

        String message = "error";
        Users user = userService.findById(id);

        if(user!=null){
            if(newPassword.equals(reNewPassword)){
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.updateUser(user);
                message = "success";
            }
        }

        return "redirect:/super/details/"+id+"?"+message;
    }

    @PostMapping(value = "/adddepartment")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addDepartment(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "organization_id") Long organizationId
    ){

        Organizations organization = userService.getOrganziationById(organizationId);
        String message = "?adderror";
        if(organization!=null){
            if(userService.addDepartment(new Departments(name, organization))!=null){
                message = "?addsuccess";
            }
        }
        return "redirect:/super/organizationdetails/"+organizationId+message;
    }


    @GetMapping(value = "/departmentdetails/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String departmentDetail(Model model, @PathVariable(name = "id") Long id){

        Departments department = userService.findDeparmentById(id);
        model.addAttribute("department", department);
        model.addAttribute("user", this.getUserData());

        return "super/departmentdetails";
    }

    @PostMapping(value = "/updatedepartment")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateDepartment(@RequestParam(name = "id") Long id,
                                   @RequestParam(name = "name") String name){

        String message = "error";
        Departments department = userService.findDeparmentById(id);

        if(department!=null){
            department.setName(name);
            userService.updateDepartment(department);
            message = "success";
        }

        return "redirect:/super/departmentdetails/"+id+"?"+message;
    }

    @GetMapping(value = "/userpermissions/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String userPermissions(Model model, @PathVariable(name = "id") Long id){

        Users userDetail = userService.findById(id);
        model.addAttribute("userDetail", userDetail);

        Set<Roles> roles = userService.getRolesByUser(userDetail);
        model.addAttribute("roles", roles);

        Set<Roles> availableRoles = userService.getAvailableRolesByUser(userDetail, true);
        model.addAttribute("availableRoles", availableRoles);

        model.addAttribute("user", this.getUserData());

        return "super/userpermissions";
    }

    @PostMapping(value = "/adduserpermission")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addUserPermission(@RequestParam(name = "user_id") Long userId,
                                    @RequestParam(name = "role_id") Long roleId){

        Users user = userService.findById(userId);
        Roles role = userService.getRoleById(roleId);

        if(user!=null&&role!=null){
            Permissions permission = new Permissions(user, role, false);
            userService.addPermission(permission);
        }

        return "redirect:/super/userpermissions/"+userId;
    }


    @PostMapping(value = "/removeuserpermission")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeUserPermission(@RequestParam(name = "user_id") Long userId,
                                       @RequestParam(name = "role_id") Long roleId){

        Users user = userService.findById(userId);
        Roles role = userService.getRoleById(roleId);

        if(user!=null&&role!=null){
            Permissions permission = new Permissions(user, role, true);
            userService.addPermission(permission);
        }

        return "redirect:/super/userpermissions/"+userId;
    }

    @PostMapping(value = "/resetpermissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String resetUserPermissions(@RequestParam(name = "user_id") Long userId){
        Users user = userService.findById(userId);
        if(user!=null){
            userService.resetPermissions(user);
        }
        return "redirect:/super/userpermissions/"+userId;
    }

    @GetMapping(value = "/groupdetails/{groupId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String groupDetails(Model model, @PathVariable(name = "groupId") Long groupId){

        Groups group = userService.getGroup(groupId);

        Users currentUser = getUserData();
        model.addAttribute("user", currentUser);

        if(group!=null) {
            Set<Roles> availableRoles = userService.getAvailableRolesByGroup(group);

            model.addAttribute("group", group);
            model.addAttribute("availableRoles", availableRoles);

            return "super/groupdetails";
        }else {
            return "404";
        }
    }

    @GetMapping(value = "/currencydetails/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String currencyDetail(Model model, @PathVariable(name = "id") Long id){

        Currencies currency = userService.findCurrencyById(id);
        model.addAttribute("currency", currency);
        model.addAttribute("user", this.getUserData());

        return "super/currencydetails";
    }




    @PostMapping(value = "/updategroup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateGroup(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "group_name") String name,
                              @RequestParam(name = "group_description") String description){

        String message = "error";
        Groups group = userService.getGroup(id);

        if(group!=null){
            group.setName(name);
            group.setDescription(description);
            userService.saveGroup(group);
            message = "success";
        }

        return "redirect:/super/groupdetails/"+id+"?"+message;
    }

    @PostMapping(value = "/addgrouprole")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addGroupRole(@RequestParam(name = "group_id") Long id,
                               @RequestParam(name = "role_id") Long roleId){

        Groups group = userService.getGroup(id);
        Roles role = userService.getRoleById(roleId);
        group.getRoles().add(role);

        if(group!=null){
            userService.saveGroup(group);
        }

        return "redirect:/super/groupdetails/"+id;
    }

    @PostMapping(value = "/removegrouprole")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeGroupRole(@RequestParam(name = "group_id") Long id,
                                  @RequestParam(name = "role_id") Long roleId){

        Groups group = userService.getGroup(id);
        Roles role = userService.getRoleById(roleId);
        group.getRoles().remove(role);

        if(group!=null){
            userService.saveGroup(group);
        }

        return "redirect:/super/groupdetails/"+id;
    }

    @GetMapping(value = "/addgroup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addGroup(Model model){

        model.addAttribute("user", this.getUserData());
        return "super/addgroup";

    }

    @PostMapping(value = "/addgroup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addGroup(@RequestParam(name = "name") String name,
                           @RequestParam(name = "description") String description){

        String message = "?error";
        if(userService.addGroup(new Groups(name, description, null))!=null){
            message = "?success";
        }

        return "redirect:/super/addgroup"+message;
    }

    @GetMapping(value = "/addorganization")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addaOrganization(Model model){

        model.addAttribute("user", this.getUserData());
        return "super/addorganization";

    }

    @PostMapping(value = "/addorganization")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addOrganization(@RequestParam(name = "organization_name") String name,
                                  @RequestParam(name = "checkboxName", required = false) int value){

        String message = "error";
        if(userService.addOrganization(new Organizations(name, value))!=null){
            message = "?success";
        }

        return "redirect:/super/listorganizations"+message;
    }


    @GetMapping(value = "/addcurrency")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String addCurrency(Model model){

        model.addAttribute("user", this.getUserData());
        return "super/addcurrency";
    }


    @PostMapping(value = "/deletegroup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String deleteGroup(@RequestParam(name = "group_id") Long id){

        String message = "?error";
        Groups group = userService.getGroup(id);
        if(userService.deleteGroup(group)!=null){
            message = "?success";
        }

        return "redirect:/super/listgroups";
    }

    @GetMapping(value = "/listorganizations")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String listOrganizations(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        int pageCount = userService.countAllOrganizations();

        if(page<1){
            page = 1;
        }

        int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
        if(pageCountFinal==0){
            pageCountFinal = 1;
        }

        List<Organizations> organizations = userService.getAllPagingOrganizations(page-1, StaticConfig.pageSize);
        model.addAttribute("pageCount", pageCountFinal);
        model.addAttribute("organizations", organizations);
        model.addAttribute("user", this.getUserData());

        return "super/listorganizations";
    }

    @GetMapping(value = "/organizationdetails/{orgId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String organizationDetails(Model model,
                                      @PathVariable(name = "orgId") Long orgId,
                                      @RequestParam(name = "page", defaultValue = "1") int page){

        Organizations organization = userService.getOrganziationById(orgId);

        Users currentUser = getUserData();
        model.addAttribute("user", currentUser);

        if(organization!=null) {

            model.addAttribute("organization", organization);

            int pageCount = userService.countDepartmentsByOrganization(organization);

            if(page<1){
                page = 1;
            }

            int pageCountFinal = (int) (Math.ceil((double) pageCount / StaticConfig.pageSize));
            if(pageCountFinal==0){
                pageCountFinal = 1;
            }

            List<Departments> departments = userService.getAllPagingDepartmentsByOrganization(page-1, StaticConfig.pageSize, organization);
            model.addAttribute("pageCount", pageCountFinal);
            model.addAttribute("departments", departments);
            model.addAttribute("user", this.getUserData());

            return "super/organizationdetails";

        }else {

            return "404";

        }
    }

    @PostMapping(value = "/updateorganization")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateOrganization(@RequestParam(name = "id") Long id,
                                     @RequestParam(name = "organization_name") String name){

        String message = "error";
        Organizations organization = userService.getOrganziationById(id);

        if(organization!=null){
            organization.setName(name);
            userService.updateOrganization(organization);
            message = "success";
        }

        return "redirect:/super/organizationdetails/"+id+"?"+message;
    }

    @GetMapping(value = "/removeuser/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeUser(Model model, @PathVariable(name = "userId") Long userId){


        Users user = userService.findById(userId);

        Users currentUser = getUserData();
        model.addAttribute("user", currentUser);

        if(user!=null) {
            Set<Roles> availableRoles = userService.getRolesByUser(user);

            model.addAttribute("user", user);
            model.addAttribute("availableRoles", availableRoles);

            return "super/removeuser";
        }else {
            return "404";
        }
    }

    @PostMapping(value = "/removeuser")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeUserById(@RequestParam(name = "user_id") Long id){
        String message = "?error";
        Users user = userService.findById(id);
        if(user!=null){
            user.setDeletedAt(new Date());
            userService.updateUser(user);
            message = "?success";
        }
        return "redirect:/super/listusers"+message;
    }



    @PostMapping(value = "/removeorganization")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeOrganization(@RequestParam(name = "id") Long id){
        String message = "?error";
        Organizations organization = userService.getOrganziationById(id);
        if(organization!=null){
            organization.setDeletedAt(new Date());
            userService.updateOrganization(organization);
        }
        return "redirect:/super/listorganizations"+message;
    }

    @PostMapping(value = "/removedepartment")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeDepartment(@RequestParam(name = "id") Long id){
        Departments department = userService.findDeparmentById(id);
        String redirect = "listorganizations";
        if(department!=null){
            redirect = "organizationdetails/"+department.getOrganization().getId();
            department.setDeletedAt(new Date());
            userService.updateDepartment(department);
        }
        return "redirect:/super/"+redirect;
    }

    @PostMapping(value = "/removecurrency")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String removeCurrency(@RequestParam(name = "id") Long id){

        String message = "?error";
        Currencies currency = userService.findCurrencyById(id);
        if(userService.deleteCurrency(currency)!=null){
            message = "?success";
        }

        return "redirect:/super/listcurrencies";
    }
}
