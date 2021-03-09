package kz.bitlab.robygroup.sppmid.core.services.impl;

import kz.bitlab.robygroup.sppmid.core.config.StaticConfig;
import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.user.*;
import kz.bitlab.robygroup.sppmid.core.repository.*;
import kz.bitlab.robygroup.sppmid.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableWebSecurity
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private GroupsPagingRepository groupsPagingRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Users> userOpt = userRepository.findByLoginAndDeletedAtNull(login);
        if(userOpt.isPresent()){
            Users user = userOpt.get();
            Set<Roles> roles = new HashSet<>(user.getGroup().getRoles());

            List<Permissions> permissions = permissionRepository.findAllByUser_Id(user.getId());

            Set<Roles> included = permissions.parallelStream()
                    .filter(e -> !e.isExcluded())
                    .collect(Collectors.toSet())
                    .parallelStream().map(e -> e.getRole())
                    .collect(Collectors.toSet());

            Set<Roles> excluded = permissions.parallelStream()
                    .filter(e -> e.isExcluded())
                    .collect(Collectors.toSet())
                    .parallelStream().map(e -> e.getRole())
                    .collect(Collectors.toSet());

            roles.addAll(included);
            roles.removeAll(excluded);

            User securityUser = new User(user.getLogin(), user.getPassword(), roles);
            return securityUser;
        }else{
            return null;
        }
    }

    public Users findUserByEmail(String email){
        Optional<Users> user = userRepository.findUserByEmail(email);
        return user.orElse(null);
    }

    public Users findByLogin(String login){
        Optional<Users> userOpt = userRepository.findByLoginAndDeletedAtNull(login);
        return userOpt.orElse(null);
    }

    @Override
    @Transactional
    public Users register(Users user) {
        Users found = userRepository.findByLoginAndDeletedAtNull(user.getLogin()).orElse(null);
        if(found==null) {
            return userRepository.save(user);
        }else{
            return null;
        }
    }

    public Users save(Users user){
        if(user!=null){
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public List<Groups> getAllGroups(boolean isSuperAdmin) {
        return (isSuperAdmin?groupRepository.findAllByDeletedAtNull():groupRepository.findAllByDeletedAtNullAndIdNot(StaticConfig.SUPER_ADMIN_GROUP));
    }

    @Override
    public Groups getGroup(Long id) {
        Optional<Groups> group = groupRepository.findByIdAndDeletedAtNull(id);
        return group.orElse(null);
    }

    @Override
    public Groups getGroupByName(String name) {
        Optional<Groups> group = groupRepository.findByNameAndDeletedAtNull(name);
        return group.orElse(null);
    }

    @Override
    public List<Users> getAllPagingUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAllByDeletedAtNull(pageable);
    }

    @Override
    public List<Currencies> getAllCurrencies(boolean isSuperAdmin) {
        return (isSuperAdmin?currencyRepository.findAllByDeletedAtNull():currencyRepository.findAllByDeletedAtNullAndIdNot(StaticConfig.SUPER_ADMIN_GROUP));
    }

    @Override
    public Users findById(Long id){
        return userRepository.findByIdAndDeletedAtNull(id).orElse(null);
    }



    @Override
    public Users updateUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Departments findDeparmentById(Long id) {
        Optional<Departments> department = departmentRepository.findById(id);
        return department.orElse(null);
    }

    @Override
    public Departments addDepartment(Departments department) {
        return departmentRepository.save(department);
    }
    @Override
    public List<Departments> getAllDepartments() {
        return departmentRepository.findAllByDeletedAtNull();
    }

    @Override
    public Departments updateDepartment(Departments department) {
        return departmentRepository.save(department);
    }


    @Override
    public int countUsersByOrganization(Organizations organization) {
        return userRepository.countAllByDepartment_OrganizationAndDeletedAtNull(organization);
    }

    @Override
    public List<Users> getZayavitelsByDepartment(Departments department) {
        Roles role = rolesRepository.findByDeletedAtNullAndRole("ROLE_ZAYAVITEL");
        return userRepository.findAllByDeletedAtNullAndDepartmentAndGroup_Roles(department, role);
    }

    @Override
    public List<Users> getIspolnitelsByDepartment(Departments department) {
        Roles role = rolesRepository.findByDeletedAtNullAndRole("ROLE_ISPOLNITEL_UBP_MID");
        return userRepository.findAllByDeletedAtNullAndDepartmentAndGroup_Roles(department, role);
    }

    @Override
    public List<Users> getPoliticalExecutorsByOrganizationAndGroup(Organizations organization, Groups group) {
        return userRepository.findAllByDeletedAtNullAndOrganizationIdAndGroup(organization, group);
    }

    @Override
    public int countUsers() {
        return userRepository.countAllByDeletedAtNull();
    }

    @Override
    public List<Departments> getAllPagingDepartmentsByOrganization(int pageNo, int pageSize, Organizations organization) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return departmentRepository.findAllByDeletedAtNullAndOrganization(pageable, organization);
    }

    @Override
    public int countDepartmentsByOrganization(Organizations organization) {
        return departmentRepository.countAllByDeletedAtNullAndOrganization(organization);
    }

    @Override
    public List<Groups> getAllPagingGroups(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return groupsPagingRepository.findAllByDeletedAtNull(pageable);
    }

    @Override
    public int countGroups() {
        return groupRepository.countAllByDeletedAtNull();
    }

    @Override
    public Set<Roles> getRolesByUser(Users user) {

        Set<Roles> roles = new HashSet<>(user.getGroup().getRoles());
        List<Permissions> permissions = permissionRepository.findAllByUser_Id(user.getId());

        Set<Roles> included = permissions.parallelStream()
                .filter(e -> !e.isExcluded())
                .collect(Collectors.toSet())
                .parallelStream().map(e -> e.getRole())
                .collect(Collectors.toSet());

        Set<Roles> excluded = permissions.parallelStream()
                .filter(e -> e.isExcluded())
                .collect(Collectors.toSet())
                .parallelStream().map(e -> e.getRole())
                .collect(Collectors.toSet());

        roles.addAll(included);
        roles.removeAll(excluded);

        return roles;

    }

    @Override
    public Set<Roles> getAvailableRolesByUser(Users user, boolean isSuperAdmin) {
        Set<Roles> availableRoles = (isSuperAdmin?rolesRepository.findAllByDeletedAtNull():rolesRepository.findAllByDeletedAtNullAndIdNot(StaticConfig.SUPER_ADMIN_ROLE));
        Set<Roles> roles = getRolesByUser(user);
        availableRoles.removeAll(roles);
        return availableRoles;
    }

    @Override
    public Roles getRoleById(Long id) {
        return rolesRepository.findByDeletedAtNullAndId(id);
    }

    @Override
    @Transactional
    public Permissions addPermission(Permissions permission) {
        permissionRepository.removeAllByUserAndRole(permission.getUser(), permission.getRole());
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public void resetPermissions(Users user) {
        permissionRepository.removeAllByUser(user);
    }

    @Override
    public Groups saveGroup(Groups group) {
        return groupRepository.save(group);
    }

    @Override
    public Set<Roles> getAvailableRolesByGroup(Groups group) {
        Set<Roles> availableRoles = rolesRepository.findAllByDeletedAtNull();
        availableRoles.removeAll(group.getRoles());
        return availableRoles;
    }

    @Override
    public Groups addGroup(Groups group) {
        return groupRepository.save(group);
    }

    @Override
    public Groups deleteGroup(Groups group) {
        group.setDeletedAt(new Date());
        return groupRepository.save(group);
    }

    @Override
    public Currencies addCurrency(Currencies currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public Currencies saveCurrency(Currencies currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public Currencies findCurrencyById(Long id) {
        return currencyRepository.findByDeletedAtNullAndId(id).orElse(null);
    }

    @Override
    public Currencies deleteCurrency(Currencies currency) {
        currency.setDeletedAt(new Date());
        return currencyRepository.save(currency);
    }

    @Override
    public Organizations getOrganziationById(Long id) {
        Optional<Organizations> organization = organizationRepository.findByIdAndDeletedAtNull(id);
        return organization.orElse(null);
    }

    @Override
    public Organizations addOrganization(Organizations organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public List<Organizations> getAllOrganizations() {
        return organizationRepository.findAllByDeletedAtNullOrderByIdAsc();
    }

    @Override
    public Organizations updateOrganization(Organizations organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public List<Organizations> getAllPagingOrganizations(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return organizationRepository.findAllByDeletedAtNullOrderByIdAsc(pageable);
    }

    @Override
    public int countAllOrganizations() {
        return organizationRepository.countAllByDeletedAtNull();
    }

    @Override
    public List<Departments> getAllDepartmentsByOrganization(Organizations organization) {
        return departmentRepository.findAllByOrganizationAndDeletedAtNull(organization);
    }

    @Override
    public List<Users> getPagingUsersByOrganization(Organizations organization, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepository.findAllByDepartmentOrganizationAndDeletedAtNull(organization, pageable);
    }
}
