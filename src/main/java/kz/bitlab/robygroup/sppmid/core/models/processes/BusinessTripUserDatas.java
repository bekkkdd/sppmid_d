package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_business_trip_user_datas",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripUserDatas extends BaseEntity {

    @Column(name = "iin")
    private String iin;

    @Column(name = "fio_data")
    private String fioData;

    @Column(name = "is_gos_servant")
    private boolean isGosServant;

    @Column(name = "position")
    private String position;

    @Column(name = "is_abroadable")
    private boolean isAbroadable;

    @Column(name = "have_debts")
    private boolean hasDebts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_trip_id")
    private BusinessTrips businessTrip;

}
