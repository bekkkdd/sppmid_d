package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<Users, Long> {

    Optional<Users> findByLoginAndDeletedAtNull(String login);

    Optional<Users> findByIdAndDeletedAtNull(Long id);

    void deleteById(Long id);

    int countAllByDepartment_OrganizationAndDeletedAtNull(Organizations organization);

    int countAllByDeletedAtNull();

    List<Users> findAllByDeletedAtNull(Pageable pageable);

    List<Users> findAllByDepartmentOrganizationAndDeletedAtNull(Organizations organization, Pageable pageable);

    Optional<Users> findUserByEmail(String email);

    Users save(Users user);

    List<Users> findAllByDeletedAtNullAndDepartmentAndGroup_Roles(Departments department, Roles role);

    @Query("SELECT u FROM Users u LEFT JOIN Departments d on u.department=d LEFT JOIN Organizations o on d.organization = o WHERE o = ?1 and u.group = ?2")
    List<Users> findAllByDeletedAtNullAndOrganizationIdAndGroup(Organizations organization, Groups group);
}
