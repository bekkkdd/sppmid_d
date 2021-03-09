package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organizations, Long> {

    List<Organizations> findAllByDeletedAtNullOrderByIdAsc();
    List<Organizations> findAllByIsMidRkAndDeletedAtNullOrderByIdAsc(int isMidRk);
    Optional<Organizations> findByIdAndDeletedAtNull(Long id);
    List<Organizations> findAllByDeletedAtNullOrderByIdAsc(Pageable pageable);
    int countAllByDeletedAtNull();

}