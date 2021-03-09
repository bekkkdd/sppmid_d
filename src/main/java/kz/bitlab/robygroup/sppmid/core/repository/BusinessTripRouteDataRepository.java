package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripRouteData;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTripRouteDataRepository extends JpaRepository<BusinessTripRouteData, Long> {

    List<BusinessTripRouteData> findAllByDeletedAtNullAndBusinessTripIdOrderByIdAsc(Long id);

    void removeBusinessTripRouteDataByIdAndBusinessTrip(Long id, BusinessTrips businessTrips);

}