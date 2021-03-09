package kz.bitlab.robygroup.sppmid.core.models.user;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Nurlan Altynbek
 * @date 2/18/20
 * @project sppmid
 **/
@Data
@Table(name = "messages")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_messages",
        initialValue = 1,
        allocationSize = 1
)
public class Message extends BaseEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "message")
    private String message;
}
