package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.currency.Currencies;
import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import kz.bitlab.robygroup.sppmid.core.models.user.Departments;
import kz.bitlab.robygroup.sppmid.core.models.user.Organizations;
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
        sequenceName = "s_main_processes",
        initialValue = 1,
        allocationSize = 1
)
public class MainProcess extends BaseEntity {

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organizations organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gos_organ_id")
    private Organizations gosOrganization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Departments department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id")
    private Currencies currency;

    @Column(name = "limit_value")
    private int limitValue;

    @Column(name = "initial_limit_value")
    private int initialLimitValue;

    @Column(name = "first_year")
    private int firstYear;

    @Column(name = "second_year")
    private int secondYear;

    @Column(name = "third_year")
    private int thirdYear;

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

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_combined")
    private int isCombined;

    @Column(name = "is_uncombinable")
    private int isUncombinable;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "process_mid_perfomers",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> midPerformers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "process_go_perfomers",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> goPerformers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "process_section_datas",
            joinColumns = @JoinColumn(name = "process_id"),
            inverseJoinColumns = @JoinColumn(name = "section_data_id")
    )
    private Set<SectionData> sectionData;

}
