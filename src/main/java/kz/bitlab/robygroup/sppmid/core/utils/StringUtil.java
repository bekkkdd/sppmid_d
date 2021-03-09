package kz.bitlab.robygroup.sppmid.core.utils;

import java.util.Random;

/**
 * @author Assylkhan
 * on 22.02.2020
 * @project sppmid
 */
public class StringUtil {

    public static String getSixDigitNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

}
