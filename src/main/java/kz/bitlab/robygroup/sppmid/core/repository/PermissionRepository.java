package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Permissions;
import kz.bitlab.robygroup.sppmid.core.models.user.Roles;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.Permission;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permissions, Long> {

    List<Permission> findAllByDeletedAtNull();
    List<Permissions> findAllByUser_Id(Long userId);
    void removeAllByUser(Users user);
    void removeAllByUserAndRole(Users user, Roles role);

}
