package kz.bitlab.robygroup.sppmid.core.integrations.rest;

import kz.bitlab.robygroup.sppmid.core.integrations.aisoip.AISOIPIntegration;
import kz.bitlab.robygroup.sppmid.core.integrations.aisoip.AISOIPUsers;
import kz.bitlab.robygroup.sppmid.core.integrations.ekyzmet.EKyzmetIntegration;
import kz.bitlab.robygroup.sppmid.core.integrations.ekyzmet.EKyzmetUsers;
import kz.bitlab.robygroup.sppmid.core.integrations.gbdfl.GBDFLIntegration;
import kz.bitlab.robygroup.sppmid.core.integrations.gbdfl.GBDFLUsers;
import kz.bitlab.robygroup.sppmid.core.integrations.onecaccounting.OneCAccountingIntegration;
import kz.bitlab.robygroup.sppmid.core.models.processes.TemporaryBusinessTripUsers;
import kz.bitlab.robygroup.sppmid.core.repository.TemporaryBusinessTripUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integration")
public class IntegrationRestController {

    @Autowired
    private AISOIPIntegration aisoipIntegration;

    @Autowired
    private EKyzmetIntegration eKyzmetIntegration;

    @Autowired
    private GBDFLIntegration gbdflIntegration;

    @Autowired
    private OneCAccountingIntegration oneCAccountingIntegration;

    @Autowired
    private TemporaryBusinessTripUserRepository temporaryBusinessTripUserRepository;

    @PostMapping("/loaduser/{iin}")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public ResponseEntity<?> getUserDataByIIN(@PathVariable(name = "iin") String iin){

        TemporaryBusinessTripUsers temporaryBusinessTripUser = new TemporaryBusinessTripUsers();

        GBDFLUsers gbdflUser = gbdflIntegration.getUserByIIN(iin);

        if(gbdflUser!=null){

            temporaryBusinessTripUser.setIin(gbdflUser.getIin());
            temporaryBusinessTripUser.setFioData(gbdflUser.getFioData());

        }

        EKyzmetUsers eKyzmetUser = eKyzmetIntegration.getUserByIIN(iin);

        if(eKyzmetUser!=null){

            temporaryBusinessTripUser.setPosition(eKyzmetUser.getPosition());
            temporaryBusinessTripUser.setGosServant(eKyzmetUser.isGosServant());

        }

        temporaryBusinessTripUser.setAbroadable(aisoipIntegration.checkAbroadableByIIN(iin));
        temporaryBusinessTripUser.setHasDebts(oneCAccountingIntegration.checkHasDebtsByIIN(iin));
        temporaryBusinessTripUserRepository.removeAllByIin(iin);
        temporaryBusinessTripUserRepository.save(temporaryBusinessTripUser);

        return new ResponseEntity<>("OK", HttpStatus.OK);

    }

    @PostMapping("/gettempuser/{iin}")
    @PreAuthorize("hasRole('ROLE_ZAYAVITEL')")
    public ResponseEntity<?> getUserTempUserDataByIIN(@PathVariable(name = "iin") String iin){

        TemporaryBusinessTripUsers temporaryBusinessTripUser = temporaryBusinessTripUserRepository.findByDeletedAtNullAndIin(iin);
        if(temporaryBusinessTripUser!=null){
            return new ResponseEntity<>(temporaryBusinessTripUser, HttpStatus.OK);
        }
        return new ResponseEntity<>("ERROR", HttpStatus.OK);

    }

}