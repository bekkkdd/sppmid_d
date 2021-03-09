package kz.bitlab.robygroup.sppmid.core.services;

/**
 * @author Nurlan Altynbek
 * @date 2/17/20
 * @project sppmid
 **/
public interface MailService {

    void sendSimpleMessage(String to, String subject, String text);

}
