package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DepartmentRepository extends PagingAndSortingRepository<Departments, Long> {

    List<Departments> findAllByDeletedAtNull();
    int countAllByDeletedAtNull();
    List<Departments> findAllByDeletedAtNull(Pageable pageable);
    List<Departments> findAllByOrganizationAndDeletedAtNull(Organizations organization);
    int countAllByDeletedAtNullAndOrganization(Organizations organization);
    List<Departments> findAllByDeletedAtNullAndOrganization(Pageable pageable, Organizations organization);
}
