package kz.bitlab.robygroup.sppmid.core.integrations.aisoip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISOIPUsers {

    private String iin;
    private String fioData;
    private boolean isAbroadable;

}