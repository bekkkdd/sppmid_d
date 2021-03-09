package kz.bitlab.robygroup.sppmid.core.integrations.onecaccounting;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("singleton")
public class OneCAccountingIntegration {

    private ArrayList<OneCAccountingUser> oneCAccountingUsers;

    public OneCAccountingIntegration(){
        oneCAccountingUsers = new ArrayList<>();
        oneCAccountingUsers.add(new OneCAccountingUser("890526301844", "Жуанышев Ильяс Оразгалиевич", false));
        oneCAccountingUsers.add(new OneCAccountingUser("900526301844", "Жуанышев Алмас Ассетович", false));
        oneCAccountingUsers.add(new OneCAccountingUser("910526301844", "Ассетов Ерлан Оразгалиевич", false));
        oneCAccountingUsers.add(new OneCAccountingUser("920526301844", "Сагымбай Нурбол Оразгалиевич", false));
        oneCAccountingUsers.add(new OneCAccountingUser("930526301844", "Жуанышев Ержан Нуболович", true));
        oneCAccountingUsers.add(new OneCAccountingUser("940526301844", "Нусипбеков Жасулан Ерикович", false));
        oneCAccountingUsers.add(new OneCAccountingUser("950526301844", "Байзакова Айжан", true));
        oneCAccountingUsers.add(new OneCAccountingUser("960526301844", "Советов Нурбол Шынгысович", false));
        oneCAccountingUsers.add(new OneCAccountingUser("970526301844", "Асылбеков Султан Кайратович", false));
        oneCAccountingUsers.add(new OneCAccountingUser("980526301844", "Сабитова Мадина Нурдаулетовна", false));
        oneCAccountingUsers.add(new OneCAccountingUser("990526301844", "Нусипбеков Нуржан Ермекович", false));
    }

    public boolean checkHasDebtsByIIN(String iin){
        for(OneCAccountingUser user : oneCAccountingUsers){
            if(user.getIin().equals(iin)){
                return user.isHasDebts();
            }
        }
        return false;
    }

}