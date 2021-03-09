package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPagingRepository extends PagingAndSortingRepository<Users, Long> {

}
