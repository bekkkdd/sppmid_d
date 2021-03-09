package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripPurpose;
import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BusinessTripPurposeRepository extends JpaRepository<BusinessTripPurpose, Long> {

    List<BusinessTripPurpose> findAllByDeletedAtNull();
    List<BusinessTripPurpose> findAllByDeletedAtNullAndBusinessTrip(BusinessTrips businessTrips);
    BusinessTripPurpose findByDeletedAtNullAndId(Long id);

    @Transactional
    void deleteBusinessTripPurposeById(Long id);

}
