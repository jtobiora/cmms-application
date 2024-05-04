package ng.upperlink.nibss.cmms.util;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.KeySpec;

@Service
public class AESFileDecryption {

	public void decrypt(File fileTobeDecrypted, String decryptedFileLocation, String nameOfDecryptedFile, String password) throws Exception {

		// reading the salt
		// user should have secure mechanism to transfer the
		// salt, iv and password to the recipient
//		FileInputStream saltFis = new FileInputStream("salt.enc");
		byte[] salt = new byte[8];
//		saltFis.read(salt);
//		saltFis.close();

		// reading the iv
//		FileInputStream ivFis = new FileInputStream("iv.enc");
//		byte[] iv = new byte[16];
//		ivFis.read(iv);
//		ivFis.close();

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKey tmp = factory.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		//
//		Cipher cipherEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipherEncryption.init(Cipher.ENCRYPT_MODE, secret);
//		AlgorithmParameters params = cipherEncryption.getParameters();

		byte[] ivBytes = "1234567812345678".getBytes();

		// file decryption
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
		FileInputStream fis = new FileInputStream(fileTobeDecrypted);
		FileOutputStream fos = new FileOutputStream(new File(decryptedFileLocation+"\\"+nameOfDecryptedFile));

		byte[] in = new byte[64];
		int read;
		while ((read = fis.read(in)) != -1) {
			byte[] output = cipher.update(in, 0, read);
			if (output != null)
				fos.write(output);
		}

		byte[] output = cipher.doFinal();
		if (output != null)
			fos.write(output);
		fis.close();
		fos.flush();
		fos.close();
		System.out.println("File Decrypted.");
	}

	public static String encrypt(String strClearText) throws Exception{
		String strData="";
		String strKey = "skls7979SGS%S*^*S:Lk;43435";

		try {
			SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
			Cipher cipher=Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted=cipher.doFinal(strClearText.getBytes());
			strData=new String(encrypted);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

//	public static void main(String[] args) throws Exception {
//		decrypt(new File("C:\\ProgramData\\999901180412123516325468715700.des"),"decrypted.xml","password");
//	}
}