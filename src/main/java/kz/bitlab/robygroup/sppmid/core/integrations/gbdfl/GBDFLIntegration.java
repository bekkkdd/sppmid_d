package kz.bitlab.robygroup.sppmid.core.integrations.gbdfl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("singleton")
public class GBDFLIntegration {

    private ArrayList<GBDFLUsers> gbdflUsers;

    public GBDFLIntegration(){
        gbdflUsers = new ArrayList<>();
        gbdflUsers.add(new GBDFLUsers("890526301844", "Жуанышев Ильяс Оразгалиевич"));
        gbdflUsers.add(new GBDFLUsers("900526301844", "Жуанышев Алмас Ассетович"));
        gbdflUsers.add(new GBDFLUsers("910526301844", "Ассетов Ерлан Оразгалиевич"));
        gbdflUsers.add(new GBDFLUsers("920526301844", "Сагымбай Нурбол Оразгалиевич"));
        gbdflUsers.add(new GBDFLUsers("930526301844", "Жуанышев Ержан Нуболович"));
        gbdflUsers.add(new GBDFLUsers("940526301844", "Нусипбеков Жасулан Ерикович"));
        gbdflUsers.add(new GBDFLUsers("950526301844", "Байзакова Айжан"));
        gbdflUsers.add(new GBDFLUsers("960526301844", "Советов Нурбол Шынгысович"));
        gbdflUsers.add(new GBDFLUsers("970526301844", "Асылбеков Султан Кайратович"));
        gbdflUsers.add(new GBDFLUsers("980526301844", "Сабитова Мадина Нурдаулетовна"));
        gbdflUsers.add(new GBDFLUsers("990526301844", "Нусипбеков Нуржан Ермекович"));
    }

    public GBDFLUsers getUserByIIN(String iin){
        for(GBDFLUsers user : gbdflUsers){
            if(user.getIin().equals(iin)){
                return user;
            }
        }
        return null;
    }

}
