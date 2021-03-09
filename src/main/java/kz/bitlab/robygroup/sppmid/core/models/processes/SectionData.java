package kz.bitlab.robygroup.sppmid.core.models.processes;


import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
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
        sequenceName = "s_section_datas",
        initialValue = 1,
        allocationSize = 1
)
public class SectionData extends BaseEntity {

    @Column(name = "section_type")
    private int sectionType;

    @Column(name = "process_id")
    private Long processId;

    @Column(name = "initial_process_id")
    private Long initialProcessId;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "limit_time_from")
    private String limitTimeFrom;

    @Column(name = "limit_time_to")
    private String limitTimeTo;

    @Column(name = "participant_amount")
    private int participantAmount;

    @Column(name = "days_amount")
    private int daysAmount;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currencies currency;

    @Column(name = "daily_expences")
    private int dailyExpences;

    @Column(name = "residence_expences")
    private int residenceExpences;

    @Column(name = "fare_expences")
    private int fareExpences;

}
