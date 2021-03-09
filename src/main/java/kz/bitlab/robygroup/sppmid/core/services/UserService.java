package kz.bitlab.robygroup.sppmid.core.services;

import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.user.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService extends UserDetailsService {

    Users register(Users user);
    Users findByLogin(String login);
    Users findById(Long id);
    Users save(Users user);
    List<Groups> getAllGroups(boolean isSuperAdmin);
    Groups getGroup(Long id);
    Groups getGroupByName(String name);
    Groups saveGroup(Groups group);
    Groups addGroup(Groups group);
    Groups deleteGroup(Groups group);

    Currencies addCurrency(Currencies currency);
    Currencies saveCurrency(Currencies currency);
    Currencies findCurrencyById(Long id);
    Currencies deleteCurrency(Currencies currency);

    List<Users> getAllPagingUsers(int pageNo, int pageSize);

    List<Currencies> getAllCurrencies(boolean isSuperAdmin);

    int countUsers();
    Users updateUser(Users user);

    Departments findDeparmentById(Long id);
    Departments addDepartment(Departments department);
    List<Departments> getAllDepartments();
    Departments updateDepartment(Departments department);
    Organizations getOrganziationById(Long id);
    Organizations addOrganization(Organizations organization);
    Organizations updateOrganization(Organizations organization);
    List<Organizations> getAllOrganizations();
    List<Organizations> getAllPagingOrganizations(int pageNo, int pageSize);
    int countAllOrganizations();
    List<Departments> getAllDepartmentsByOrganization(Organizations organization);

    List<Users> getPagingUsersByOrganization(Organizations organization, int pageNo, int pageSize);
    int countUsersByOrganization(Organizations organization);

    List<Users> getZayavitelsByDepartment(Departments department);
    List<Users> getIspolnitelsByDepartment(Departments department);

    List<Users> getPoliticalExecutorsByOrganizationAndGroup(Organizations organization, Groups group);

    List<Departments> getAllPagingDepartmentsByOrganization(int pageNo, int pageSize, Organizations organization);
    int countDepartmentsByOrganization(Organizations organization);

    List<Groups> getAllPagingGroups(int pageNo, int pageSize);
    int countGroups();

    Set<Roles> getRolesByUser(Users user);
    Set<Roles> getAvailableRolesByUser(Users user, boolean isSuperAdmin);
    Roles getRoleById(Long id);
    Permissions addPermission(Permissions permission);
    void resetPermissions(Users user);
    Set<Roles> getAvailableRolesByGroup(Groups group);
    Users findUserByEmail(String email);


}
