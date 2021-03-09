package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Set<Roles> findAllByDeletedAtNullAndIdNot(Long id);
    Set<Roles> findAllByDeletedAtNull();
    Roles findByDeletedAtNullAndId(Long id);
    Roles findByDeletedAtNullAndRole(String role);

}
