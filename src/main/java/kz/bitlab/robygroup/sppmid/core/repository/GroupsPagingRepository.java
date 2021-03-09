package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Groups;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GroupsPagingRepository extends PagingAndSortingRepository<Groups, Long> {

    List<Groups> findAllByDeletedAtNull(Pageable pageable);

}
