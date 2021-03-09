package kz.bitlab.robygroup.sppmid.core.integrations.aisoip;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("singleton")
public class AISOIPIntegration {

    private ArrayList<AISOIPUsers> aisoipUsers;

    public AISOIPIntegration(){
        aisoipUsers = new ArrayList<>();
        aisoipUsers.add(new AISOIPUsers("890526301844", "Жуанышев Ильяс Оразгалиевич", true));
        aisoipUsers.add(new AISOIPUsers("900526301844", "Жуанышев Алмас Ассетович", false));
        aisoipUsers.add(new AISOIPUsers("910526301844", "Ассетов Ерлан Оразгалиевич", true));
        aisoipUsers.add(new AISOIPUsers("920526301844", "Сагымбай Нурбол Оразгалиевич", false));
        aisoipUsers.add(new AISOIPUsers("930526301844", "Жуанышев Ержан Нуболович", true));
        aisoipUsers.add(new AISOIPUsers("940526301844", "Нусипбеков Жасулан Ерикович", true));
        aisoipUsers.add(new AISOIPUsers("950526301844", "Байзакова Айжан", false));
        aisoipUsers.add(new AISOIPUsers("960526301844", "Советов Нурбол Шынгысович", true));
        aisoipUsers.add(new AISOIPUsers("970526301844", "Асылбеков Султан Кайратович", true));
        aisoipUsers.add(new AISOIPUsers("980526301844", "Сабитова Мадина Нурдаулетовна", true));
        aisoipUsers.add(new AISOIPUsers("990526301844", "Нусипбеков Нуржан Ермекович", true));
    }

    public boolean checkAbroadableByIIN(String iin){
        for(AISOIPUsers user : aisoipUsers){
            if(user.getIin().equals(iin)){
                return user.isAbroadable();
            }
        }
        return false;
    }

}