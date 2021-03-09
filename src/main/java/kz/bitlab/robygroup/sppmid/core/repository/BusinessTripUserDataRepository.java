package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripUserDatas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTripUserDataRepository extends JpaRepository<BusinessTripUserDatas, Long> {

    BusinessTripUserDatas findByDeletedAtNullAndIinAndBusinessTripId(String iin, Long tripId);
    List<BusinessTripUserDatas> findAllByDeletedAtNullAndBusinessTripId(Long id);

}
