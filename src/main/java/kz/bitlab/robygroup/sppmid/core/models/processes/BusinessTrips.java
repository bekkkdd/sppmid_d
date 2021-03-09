package kz.bitlab.robygroup.sppmid.core.models.processes;


import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_business_trips",
        initialValue = 1,
        allocationSize = 1
)
public class BusinessTrips extends BaseEntity{

    @Column(name = "trip_character")
    private String tripCharacter;

    @Column(name = "base")
    private String base;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "business_trip_business_trip_cities",
            joinColumns = @JoinColumn(name = "business_trip_id"),
            inverseJoinColumns = @JoinColumn(name = "business_trip_city_id")
    )
    private Set<BusinessTripCities> businessTripCities;

    @Column(name = "is_official_delegation_with_president")
    private Boolean isOfficialDelegationWithPresident;

    @Column(name = "business_trip_proof")
    private String businessTripProof;

    @Column(name = "business_trip_term_proof")
    private String businessTripTermProof;

    @Column(name = "without_event_proof")
    private String withoutEventProof;

    @OneToMany(mappedBy = "businessTrip")
    private Set<BusinessTripPurpose> businessTripPurposes;

    @Column(name = "business_trip_days")
    private int businessTripDays;

    @Column(name = "is_daily")
    private Boolean isDaily;

    @Column(name = "is_event_plan")
    private Boolean isEventPlan;

    @Column(name = "is_foreign_005")
    private Boolean isForeign005;

    @Column(name = "is_one_day")
    private Boolean isOneDay;

    @Column(name = "is_reesidence")
    private Boolean is_residence;

    @Column(name = "is_at_the_expense_of_inviting_party")
    private Boolean isAtTheExpenseOfInvitingParty;

    @Column(name = "is_transport")
    private Boolean isTransport;

    @Column(name = "passport_type")
    private int passportType;

    @Column(name = "is_visa")
    private Boolean isVisa;

    @Column(name = "status")
    private int status;

    @Column(name = "status_text")
    private String statusText;

    @Column(name = "stage")
    private int stage;

    @Column(name = "stage_role")
    private String stageRole;

    @Column(name = "stage_name")
    private String stageName;

    @Column(name = "is_declined")
    private int isDeclined;

    @Column(name = "attachment6_created")
    private Date attachment6Created;

    @Column(name = "founds_allocated_at")
    private Date foundsAllocatedAt;

    @Column(name = "comment")
    private String comment;

    @Column(name = "proof_file")
    private String proofFile;

    @Column(name = "ticket_file")
    private String ticketFile;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users creator;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private Users executor;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private MainProcess process;
}
