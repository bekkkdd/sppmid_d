package kz.bitlab.robygroup.sppmid.core.controllers.admin;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.controllers.BaseController;
import kz.bitlab.robygroup.sppmid.core.services.MailService;
import kz.bitlab.robygroup.sppmid.core.models.user.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@AllArgsConstructor
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    private MailService mailService;
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/index")
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model){
        model.addAttribute("user", this.getUserData());
        return "admin/index";
    }

    @GetMapping(value = "/adduser")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(Model model){
        List<Groups> groups = userService.getAllGroups(false);
        model.addAttribute("groups", groups);
        Users currentUser = this.getUserData();
        model.addAttribute("user", currentUser);
        List<Departments> departments = userService.getAllDepartmentsByOrganization(currentUser.getDepartment().getOrganization());
        model.addAttribute("departments", departments);

        return "admin/adduser";
    }

    @PostMapping(value = "/adduser")
    @PreAuthorize("hasRole('ADMIN')")
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
        if(password.equals(rePassword)&&groupId!=StaticConfig.SUPER_ADMIN_GROUP){
            Groups group = userService.getGroup(groupId);
            Departments department = userService.findDeparmentById(departmentId);
            if(department!=null){
                Users currentUser = getUserData();
                if(department.getOrganization().getId()==currentUser.getDepartment().getOrganization().getId()) {
                    Users user = new Users(login, passwordEncoder.encode(password), group, department, firstName, lastName, middleName,email);
                    if (userService.register(user) != null) {
                        message = "?success";
                    }
                }
            }
        }
        return "redirect:/admin/adduser"+message;
    }

    @GetMapping(value = "/listusers")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model, @RequestParam(name = "page", defaultValue = "1") int page){

        Users currentUser = getUserData();
        int pageCount = userService.countUsersByOrganization(currentUser.getDepartment().getOrganization());

        if(page<1){
            page = 1;
        }

        List<Users> users = userService.getPagingUsersByOrganization(currentUser.getDepartment().getOrganization(), page-1, StaticConfig.pageSize);
        model.addAttribute("pageCount", (int)(Math.ceil((double)pageCount/StaticConfig.pageSize)));
        model.addAttribute("users", users);
        model.addAttribute("user", currentUser);

        return "admin/listusers";
    }

    @GetMapping(value = "/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String userDetail(Model model, @PathVariable(name = "id") Long id){

        List<Groups> groups = userService.getAllGroups(false);
        model.addAttribute("groups", groups);
        Users userDetail = userService.findById(id);
        model.addAttribute("userDetail", userDetail);
        model.addAttribute("user", this.getUserData());

        Users currentUser = this.getUserData();
        List<Departments> departments = userService.getAllDepartmentsByOrganization(currentUser.getDepartment().getOrganization());
        model.addAttribute("departments", departments);

        return "admin/details";
    }

    @PostMapping(value = "/updateuser")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@RequestParam(name = "id") Long id,
                             @RequestParam(name = "user_login") String login,
                             @RequestParam(name = "firstname") String firstName,
                             @RequestParam(name = "lastname") String lastName,
                             @RequestParam(name = "email", required = false, defaultValue = "") String email,
                             @RequestParam(name = "middlename") String middleName,
                             @RequestParam(name = "group_id") Long groupId,
                             @RequestParam(name = "department_id") Long departmentId){

        String message = "error";

        if(groupId!=StaticConfig.SUPER_ADMIN_GROUP) {

            Users currentUser = this.getUserData();
            Departments department = userService.findDeparmentById(departmentId);

            if(department!=null&&department.getOrganization().getId()==currentUser.getDepartment().getOrganization().getId()) {

                Users user = userService.findById(id);
                Groups group = userService.getGroup(groupId);

                if (user != null) {
                    user.setLogin(login);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setMiddleName(middleName);
                    user.setGroup(group);
                    user.setDepartment(department);
                    user.setEmail(email);
                    userService.updateUser(user);
                    message = "success";
                }
            }
        }

        return "redirect:/admin/details/"+id+"?"+message;
    }

    @PostMapping(value = "/updatepassword")
    @PreAuthorize("hasRole('ADMIN')")
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

        return "redirect:/admin/details/"+id+"?"+message;
    }

    @GetMapping(value = "/userpermissions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String userPermissions(Model model, @PathVariable(name = "id") Long id){

        Users userDetail = userService.findById(id);
        model.addAttribute("userDetail", userDetail);

        Set<Roles> roles = userService.getRolesByUser(userDetail);
        model.addAttribute("roles", roles);

        Set<Roles> availableRoles = userService.getAvailableRolesByUser(userDetail, false);
        model.addAttribute("availableRoles", availableRoles);

        model.addAttribute("user", this.getUserData());

        return "admin/userpermissions";
    }

    @PostMapping(value = "/adduserpermission")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUserPermission(@RequestParam(name = "user_id") Long userId,
                                    @RequestParam(name = "role_id") Long roleId){

        Users user = userService.findById(userId);
        Roles role = userService.getRoleById(roleId);

        if(user!=null&&role!=null){
            Permissions permission = new Permissions(user, role, false);
            userService.addPermission(permission);
        }

        return "redirect:/admin/userpermissions/"+userId;
    }


    @PostMapping(value = "/removeuserpermission")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeUserPermission(@RequestParam(name = "user_id") Long userId,
                                    @RequestParam(name = "role_id") Long roleId){

        Users user = userService.findById(userId);
        Roles role = userService.getRoleById(roleId);

        if(user!=null&&role!=null){
            Permissions permission = new Permissions(user, role, true);
            userService.addPermission(permission);
        }

        return "redirect:/admin/userpermissions/"+userId;
    }

    @PostMapping(value = "/resetpermissions")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetUserPermissions(@RequestParam(name = "user_id") Long userId){
        Users user = userService.findById(userId);
        if(user!=null){
            userService.resetPermissions(user);
        }
        return "redirect:/admin/userpermissions/"+userId;
    }

    @GetMapping(value = "/listprocess")
    @PreAuthorize("hasRole('ADMIN')")
    public String listProcesses(Model model){
        Users currentUser = getUserData();
        model.addAttribute("user", currentUser);
        return "admin/processlist";
    }

    @GetMapping(value = "/startprocess/{processId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String startProcess(Model model, @PathVariable(name = "processId") String processId){
        Users currentUser = getUserData();
        model.addAttribute("user", currentUser);
        model.addAttribute("processId", processId);
        return "admin/startprocess";
    }

    @PostMapping(value = "/removeuser")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeUserById(@RequestParam(name = "user_id") Long id){
        String message = "?error";
        Users user = userService.findById(id);
        if(user!=null){
            user.setDeletedAt(new Date());
            userService.updateUser(user);
            message = "?success";
        }
        return "redirect:/admin/listusers"+message;
    }


}
