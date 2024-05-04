package ng.upperlink.nibss.cmms.util.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.lang.RandomStringUtils;

public class EncyptionUtil {

    private final static String SHA_512 = "SHA-512";
    private final static String SHA_256 = "SHA-256";

    private static Logger LOG = LoggerFactory.getLogger(EncyptionUtil.class);

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom(); // cryptograph. secure random
        keyGen.init(random);
        SecretKey secretKey = keyGen.generateKey();

        byte[] raw = secretKey.getEncoded();

        return org.apache.commons.codec.binary.Base64.encodeBase64String(raw).substring(0, 16);
    }

    public static String bytesToHex(final byte[] data) {
        int len = data.length;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                b.append("0").append(Integer.toHexString(data[i] & 0xFF));
            } else {
                b.append(Integer.toHexString(data[i] & 0xFF));
            }
        }
        return b.toString();
    }

    public static byte[] hexToBytes(final String str) {
        if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    public static String generateSha256(String originalString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return sha256BytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unable to generateSha256 of {} ", originalString, e);
        }

        return "";
    }

    private static String sha256BytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static synchronized String doSHA512Encryption(String password) throws RuntimeException {
        assert  null != password && !password.isEmpty() : "password cannot be null or empty";
        try {
            return  doEncryption(SHA_512,password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA_512 + " algorithm not found",e);
        }
    }

    public static synchronized String doSHA512Encryption(String password, String salt) throws  RuntimeException {
        assert  null != password && !password.isEmpty() : "password cannot be null or empty";
        assert  null != salt && !salt.isEmpty() : "salt cannot be null or empty";

        try {
            return doEncryption(SHA_512, password, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA_512 + " algorithm not found",e);
        }

    }

    public static synchronized String doSHA256Encryption(String password) throws RuntimeException {
        assert null != password && !password.isEmpty() : " password cannot be null or empty";

        try {
            return doEncryption(SHA_256, password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA_256 + " algorithm not found",e);
        }
    }

    public static synchronized String doSHA256Encryption(String password, String salt) throws RuntimeException {
        assert  null != password && !password.isEmpty() : "password cannot be null or empty";
        assert  null != salt && !salt.isEmpty() : "salt cannot be null or empty";

        try {
            return doEncryption(SHA_256, password, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA_256 + " algorithm not found",e);
        }
    }


    private  static String doEncryption(String algorithm, String password) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        return digestAndGetPassword(digest, password);

    }

    private  static String doEncryption(String algorithm, String password, String salt) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(salt.getBytes(StandardCharsets.UTF_8));
        return digestAndGetPassword(digest, password);


    }

    private static String digestAndGetPassword(MessageDigest digest, String password) {

        byte[] pass = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder builder = new StringBuilder();
        for( int i=0; i<pass.length;i++) {
            builder.append(Integer.toString((pass[i] & 0xff) + 0x100, 16).substring(1));
        }

        return builder.toString();

    }
    
     public static String generateString(int length, boolean useLetters, boolean useNumbers) {
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }
}
