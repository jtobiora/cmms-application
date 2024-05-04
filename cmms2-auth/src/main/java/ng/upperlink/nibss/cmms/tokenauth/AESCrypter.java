/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.tokenauth;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class AESCrypter {

    private String iv;
    private String secretkey;
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    public AESCrypter(String keyz, String ivStr) {
        ivspec = new IvParameterSpec(ivStr.getBytes());
        keyspec = new SecretKeySpec(keyz.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public AESCrypter() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(secretkey.getBytes(), "AES");

        System.out.println("this ivspec = " + ivspec);

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String text) throws SecException {
     if (text == null || text.length() == 0) {
            throw new SecException("Empty string");

        }
        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new SecException("[encrypt] " + e.getMessage());
        }
        return bytesToHex(encrypted);
    }

    public String decrypt(String code) throws SecException, UnsupportedEncodingException {
        if (code == null || code.length() == 0) {
            throw new SecException("Empty string");
        }
        byte[] decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecException("[decrypt] " + e.getMessage());
        }
        return new String(decrypted, "UTF-8");
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }
        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                str = str + "0" + Integer.toHexString(data[i] & 0xFF);
            } else {
                str = str + Integer.toHexString(data[i] & 0xFF);
            }
        }
        return str;
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
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

    public static void main(String[] args){
        try{
            String iv = "5:U4w)f6J%gInpvZ";
            String key = "NIaglrTG77Ia?t9:";
            AESCrypter aesCrypter = new AESCrypter( iv,  key);
            String dec = aesCrypter.decrypt("ae5dd0bfe026a246c41efb8ab3077838daebe78d83d0c8d4ec2eee2ee8e15be6e435df8ab784eb5fd26f325ea7abdb346b625827fa6fbd2234174996106b901b282c54003dbeab99eb19d73f6eff22e59ea7182fd7910c61fdd81dd07d952b8b6e8ef124248b0831dac32100a4f02bcb6de9682e3ca93d8a5e320735af20ff8a");
            System.out.println(dec);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
