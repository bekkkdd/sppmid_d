package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripCities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTripCitiesRepository extends JpaRepository<BusinessTripCities, Long> {

    List<BusinessTripCities> findAllByDeletedAtNull();
    BusinessTripCities findByDeletedAtNullAndId(Long id);

    void deleteBusinessTripCitiesById(Long id);

}
