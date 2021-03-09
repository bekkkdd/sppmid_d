package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_business_trip_route_datas",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTripRouteData extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_trip_id")
    private BusinessTrips businessTrip;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_trip_route_id")
    private BusinessTripRoutes businessTripRoute;

    @Column(name = "date_from")
    private Date dateFrom;

    @Column(name = "date_to")
    private Date dateTo;

    @Column(name = "time_from")
    private String timeFrom;

    @Column(name = "time_to")
    private String timeTo;

    @Column(name = "days_amount")
    private int daysAmount;

    @Column(name = "is_daily")
    private boolean isDaily;

    @Column(name = "is_one_day")
    private boolean isOneDay;

    @Column(name = "is_residence")
    private boolean isResidence;

    @Column(name = "is_transport")
    private boolean isTransport;

}
