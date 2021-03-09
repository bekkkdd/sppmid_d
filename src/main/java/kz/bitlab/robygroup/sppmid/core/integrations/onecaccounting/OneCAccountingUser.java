package kz.bitlab.robygroup.sppmid.core.integrations.onecaccounting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneCAccountingUser {

    private String iin;
    private String fioData;
    private boolean hasDebts;

}
