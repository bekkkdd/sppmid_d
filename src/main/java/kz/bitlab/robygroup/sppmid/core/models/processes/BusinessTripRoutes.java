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
        sequenceName = "s_business_trip_routes",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripRoutes extends BaseEntity {

    @Column(name = "routeName")
    private String routeName;

}
