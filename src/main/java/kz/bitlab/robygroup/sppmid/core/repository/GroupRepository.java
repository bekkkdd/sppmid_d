package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups,Long> {

    List<Groups> findAllByDeletedAtNull();
    List<Groups> findAllByDeletedAtNullAndIdNot(Long id);
    Optional<Groups> findByIdAndDeletedAtNull(Long id);
    Optional<Groups> findByNameAndDeletedAtNull(String name);
    int countAllByDeletedAtNull();

}
