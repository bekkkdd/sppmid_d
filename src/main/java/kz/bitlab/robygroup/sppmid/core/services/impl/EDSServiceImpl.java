package kz.bitlab.robygroup.sppmid.core.services.impl;

import kz.bitlab.robygroup.sppmid.core.models.eds.EDSConstants;
import kz.bitlab.robygroup.sppmid.core.models.eds.EDSUser;
import kz.bitlab.robygroup.sppmid.core.services.EDSService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Assylkhan
 * on 22.02.2020
 * @project sppmid
 */
@Service
public class EDSServiceImpl implements EDSService {

    @Override
    public EDSUser getEdsUserFromFile(InputStream p12File, String password) {
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(p12File, password.toCharArray());
            Enumeration<String> e = p12.aliases();
            Map<String, String> map = new HashMap<String, String>();
            while (e.hasMoreElements()) {
                String alias = e.nextElement();
                X509Certificate c = (X509Certificate) p12.getCertificate(alias);
                Principal subject = c.getSubjectDN();
                String subjectArray[] = subject.toString().split(",");
                for (String s : subjectArray) {
                    String[] str = s.trim().split("=");
                    String key = str[0];
                    String value = str[1];
                    map.put(key, value);
                }
            }
            return EDSUser.builder()
                    .cityName(map.get(EDSConstants.ST))
                    .firstName(map.get(EDSConstants.CN))
                    .lastName(map.get(EDSConstants.SURNAME))
                    .middleName(map.get(EDSConstants.GIVEN_NAME))
                    .serialNumber(map.get(EDSConstants.SERIAL_NUMBER))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

}
