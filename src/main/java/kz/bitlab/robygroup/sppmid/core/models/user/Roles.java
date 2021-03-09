package kz.bitlab.robygroup.sppmid.core.models.user;

import kz.bitlab.robygroup.sppmid.core.models.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "seq",
        sequenceName = "s_roles",
        initialValue = 1,
        allocationSize = 1
)
public class Roles extends BaseEntity implements GrantedAuthority {

    @Column(name = "role")
    private String role;

    @Column(name = "description")
    private String description;

    @Override
    public String getAuthority() {
        return this.role;
    }
}
