package kz.bitlab.robygroup.sppmid.core.services;

import kz.bitlab.robygroup.sppmid.core.models.eds.EDSUser;

import java.io.InputStream;

/**
 * @author Assylkhan
 * on 22.02.2020
 * @project sppmid
 */
public interface EDSService {

    EDSUser getEdsUserFromFile(InputStream p12File, String password);

}
