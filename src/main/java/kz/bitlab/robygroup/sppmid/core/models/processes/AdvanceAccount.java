package kz.bitlab.robygroup.sppmid.core.models.processes;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import kz.bitlab.robygroup.sppmid.core.models.user.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_advance_account",
        initialValue = 1,
        allocationSize = 1
)
public class AdvanceAccount extends BaseEntity{
    @OneToOne
    @JoinColumn(name = "business_trip_id")
    BusinessTrips businessTrip;

    @Column(name = "boarding_pass_file")
    private String boardingPassFile;

    @ManyToOne
    @JoinColumn(name = "requester")
    private Users requester;

    @Column(name = "arrival_departure_file")
    private String arrivalDepartureFile;

    @Column(name = "bank_discharge_file")
    private String bankDischargeFile;

    @Column(name = "bank_receipt_file")
    private String bankReceiptFile;

    @Column(name = "bank_fiscal_file")
    private String bankFiscalFile;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_fio")
    private String cardFio;

    @Column(name = "card_expiration")
    private String cardExpiration;

    @Column(name = "dialog_window")
    private String dialogWindow;

    @Column(name = "stage")
    private int stage;

    @Column(name = "status")
    private int status;

    @Column(name = "status_text")
    private String statusText;
}
