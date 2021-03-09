package kz.bitlab.robygroup.sppmid.core.models.eds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Assylkhan
 * on 22.02.2020
 * @project sppmid
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EDSUser {

    private String serialNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String cityName;

}
