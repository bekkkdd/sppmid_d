package kz.bitlab.robygroup.sppmid.core.integrations.ekyzmet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EKyzmetUsers {

    private String iin;
    private String fioData;
    private boolean isGosServant;
    private String position;

}
