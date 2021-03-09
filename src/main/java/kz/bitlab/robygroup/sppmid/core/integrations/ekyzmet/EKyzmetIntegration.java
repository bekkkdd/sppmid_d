package kz.bitlab.robygroup.sppmid.core.integrations.ekyzmet;

import kz.bitlab.robygroup.sppmid.core.models.processes.BusinessTripUserDatas;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("singleton")
public class EKyzmetIntegration {

    private ArrayList<EKyzmetUsers> eKyzmetUsers;

    public EKyzmetIntegration(){
        eKyzmetUsers = new ArrayList<>();
        eKyzmetUsers.add(new EKyzmetUsers("890526301844", "Жуанышев Ильяс Оразгалиевич", true, "Советник Министра Образования"));
        eKyzmetUsers.add(new EKyzmetUsers("900526301844", "Жуанышев Алмас Ассетович", false, "Советник Министра Здравоохранения"));
        eKyzmetUsers.add(new EKyzmetUsers("910526301844", "Ассетов Ерлан Оразгалиевич", true, "Исполнитель внешней политики"));
        eKyzmetUsers.add(new EKyzmetUsers("920526301844", "Сагымбай Нурбол Оразгалиевич", true, "Переводчик"));
        eKyzmetUsers.add(new EKyzmetUsers("930526301844", "Жуанышев Ержан Нуболович", true, "Помощник Министра Спорта"));
        eKyzmetUsers.add(new EKyzmetUsers("940526301844", "Нусипбеков Жасулан Ерикович", true, "Глава ИТ Отдела АО НИТ"));
    }

    public EKyzmetUsers getUserByIIN(String iin){
        for(EKyzmetUsers user : eKyzmetUsers){
            if(user.getIin().equals(iin)){
                return user;
            }
        }
        return null;
    }

}
