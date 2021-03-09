package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.user.Groups;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currencies, Long> {

    List<Currencies> findAllByDeletedAtNull();
    Optional<Currencies> findByDeletedAtNullAndId(Long id);
    List<Currencies> findAllByDeletedAtNullAndIdNot(Long id);
    int countAllByDeletedAtNull();
    List<Currencies> findAllByDeletedAtNull(Pageable pageable);

}
