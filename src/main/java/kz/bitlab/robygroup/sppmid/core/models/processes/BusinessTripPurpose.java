package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_business_trip_purpose",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripPurpose extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="buisness_trip_id", nullable=false)
    private BusinessTrips businessTrip;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "reaching")
    private String reaching;

}
