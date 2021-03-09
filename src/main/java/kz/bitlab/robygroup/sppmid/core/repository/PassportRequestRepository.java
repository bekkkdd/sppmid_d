package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import kz.bitlab.robygroup.sppmid.core.models.processes.PassportRequest;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassportRequestRepository extends JpaRepository<PassportRequest, Long> {

    List<PassportRequest> findAllByRequester(Users requester);
    PassportRequest findByBusinessTrip(BusinessTrips businessTrip);
    PassportRequest getById(Long id);

    List<PassportRequest> findAllByStageGreaterThanEqual(int stage);
    List<PassportRequest> findAllByStageGreaterThanEqualAndRequester(int stage, Users requester);
    List<PassportRequest> findAllByStageGreaterThanEqualAndExecutor(int stage, Users executor);
}
