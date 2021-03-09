package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.TemporaryBusinessTripUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TemporaryBusinessTripUserRepository extends JpaRepository<TemporaryBusinessTripUsers, Long> {

    TemporaryBusinessTripUsers findByDeletedAtNullAndIin(String iin);
    void removeAllByIin(String iin);

}