package ng.upperlink.nibss.cmms.util;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by stanlee on 29/03/2018.
 */
public class PasswordUtil {

    public final static String CRYPT_SHA256 = "$5$";

    public static final String hashPassword(final String password, final String organisationCode, final String iv) {
        return Crypt.crypt(organisationCode.trim() + password, CRYPT_SHA256 + iv);
    }

    public static String generateRandom(final int count) {
        return RandomStringUtils.randomAscii(count);
    }
}
