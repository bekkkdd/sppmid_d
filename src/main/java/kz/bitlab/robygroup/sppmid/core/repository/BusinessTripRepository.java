package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTrips;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessTripRepository extends JpaRepository<BusinessTrips, Long> {

    List<BusinessTrips> findAllByCreator(Users creator);
    List<BusinessTrips> findAllByCreatorAndStage(Users creator, int stage);

    List<BusinessTrips> findAllByStageGreaterThanEqual(int stage);
    List<BusinessTrips> findAllByStageGreaterThanEqualAndExecutor(int stage, Users executor);
}
