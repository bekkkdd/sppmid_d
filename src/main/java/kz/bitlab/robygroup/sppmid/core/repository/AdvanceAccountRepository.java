package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import kz.bitlab.robygroup.sppmid.core.models.processes.AdvanceAccount;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvanceAccountRepository extends JpaRepository<AdvanceAccount, Long> {

    List<AdvanceAccount> findAllByRequester(Users requester);
    AdvanceAccount findByBusinessTrip(BusinessTrips businessTrip);
    AdvanceAccount getById(Long id);

    List<AdvanceAccount> findAllByStageGreaterThanEqual(int stage);
    List<AdvanceAccount> findAllByStageGreaterThanEqualAndRequester(int stage, Users requester);
//    List<AdvanceAccount> findAllByStageGreaterThanEqualAndExecutor(int stage, Users executor);
}
