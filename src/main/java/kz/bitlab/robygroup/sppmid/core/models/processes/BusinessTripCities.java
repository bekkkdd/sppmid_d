package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_business_trip_cities",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripCities extends BaseEntity {

    @Column(name = "city_name")
    private String cityName;

}
