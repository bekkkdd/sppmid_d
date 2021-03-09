package kz.bitlab.robygroup.sppmid.core.controllers.rest.superadmin;

import kz.bitlab.robygroup.sppmid.core.controllers.rest.BaseRestController;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@AllArgsConstructor
public class SuperAdminRestController extends BaseRestController {

    @Autowired
    UserService userService;

    @GetMapping("/departments/{organizationId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> listAllDepartments(@PathVariable(name = "organizationId") Long id){
        Organizations organization = userService.getOrganziationById(id);
        if(organization!=null){
            List<Departments> departments = userService.getAllDepartmentsByOrganization(organization);
            return makeResponse(departments, HttpStatus.OK);
        }else{
            return null;
        }
    }

}
