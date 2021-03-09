package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.MainProcess;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainProcessRepository extends JpaRepository<MainProcess, Long> {

    List<MainProcess> findAllByOrganizationAndStatusNotAndDeletedAtNull(Organizations organization, int status);
    List<MainProcess> findAllByGosOrganizationAndStatusNotAndDeletedAtNull(Organizations organization, int status);
    List<MainProcess> findAllByDepartmentAndStatusNotAndDeletedAtNull(Departments department, int status);

    List<MainProcess> findAllByParentIdAndDeletedAtNull(Long parentId);

    List<MainProcess> findAllByStatus(int status);

    List<MainProcess> findAllByOrganizationAndAuthorAndDeletedAtNull(Organizations organization, Users user);
    MainProcess findByIdAndDeletedAtNullAndOrganization(Long id, Organizations organization);
    MainProcess findByIdAndDeletedAtNull(Long id);
    List<MainProcess> findAllByOrganizationAndStageAndStatusAndDeletedAtNull(Organizations organization, int stage, int status);
    List<MainProcess> findAllByGosOrganizationAndStatusNotAndDeletedAtNullOrderByCreatedAtDesc(Organizations organization, int status, Pageable pageable);
    List<MainProcess> findAllByDepartmentAndStatusNotAndDeletedAtNullOrderByCreatedAtDesc(Departments department, int status, Pageable pageable);
    MainProcess findByIdAndDeletedAtNullAndGosOrganization(Long id, Organizations organization);
    List<MainProcess> findAllByDeletedAtNullAndOrganizationAndStatusNotOrderByCreatedAtDesc(Organizations organization, Pageable pageable, int status);
    int countAllByDeletedAtNullAndOrganizationAndStatusNot(Organizations organization, int status);
    int countAllByDeletedAtNullAndGosOrganizationAndStatusNotAndDeletedAtNull(Organizations organization, int status);
    int countAllByDeletedAtNullAndDepartmentAndStatusNotAndDeletedAtNull(Departments department, int status);

    List<MainProcess> findAllByDeletedAtNullAndOrganizationAndIsCombined(Organizations organizations, int isCombined);

}
