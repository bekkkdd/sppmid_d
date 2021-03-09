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
        sequenceName = "s_business_trip_aims",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripAims extends BaseEntity{

    @Column(name = "aim")
    private String aim;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_trip_id")
    private BusinessTrips businessTrip;

}
