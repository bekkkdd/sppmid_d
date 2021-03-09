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
        sequenceName = "s_passport_requests",
        initialValue = 1,
        allocationSize = 1
)
public class PassportRequest extends BaseEntity{
    @OneToOne
    @JoinColumn(name = "business_trip_id")
    BusinessTrips businessTrip;

    @Column(name = "modify_comment")
    private String modifyComment;

    @Column(name = "comment_to_requester")
    private String commentToRequester;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Users requester;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private Users executor;

    @Column(name = "stage")
    private int stage;

    @Column(name = "status")
    private int status;

    @Column(name = "status_text")
    private String statusText;

    @Column(name = "delete_reason")
    private String deleteReason;

    @Column(name = "photo3x4_file")
    private String photo3x4File;

    @Column(name = "uds_file")
    private String udsFile;
}
