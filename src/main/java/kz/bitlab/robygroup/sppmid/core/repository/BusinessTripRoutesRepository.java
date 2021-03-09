package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripRoutes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTripRoutesRepository extends JpaRepository<BusinessTripRoutes, Long> {

    List<BusinessTripRoutes> findAllByDeletedAtNull();
    BusinessTripRoutes findByDeletedAtNullAndId(Long id);

}