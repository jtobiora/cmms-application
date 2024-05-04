package ng.upperlink.nibss.cmms.encryptanddecrypt;

import ng.upperlink.nibss.cmms.dto.Response;
import ng.upperlink.nibss.cmms.enums.EncryptionHeader;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stanlee on 29/03/2018.
 */
@Service
public class NIBSSAESEncryption {

    //private InstitutionCredentialsRepo institutionCredentialsRepo;

    private static Logger LOG = LoggerFactory.getLogger(NIBSSAESEncryption.class);

    private String password;

    public static final String COMMA = ",", FULL_COLON = ":", ALGORITHM = "AES/CBC/PKCS5Padding", AES = "AES";

   /* @Autowired
    public void setInstitutionCredentialsRepo(InstitutionCredentialsRepo institutionCredentialsRepo) {
        this.institutionCredentialsRepo = institutionCredentialsRepo;
    }*/

    public static String encryptAES(String text, String secretkey, String iv)
    {
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        SecretKeySpec keyspec = new SecretKeySpec(secretkey.getBytes(), AES);
        try
        {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            return EncyptionUtil.bytesToHex(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("ERROR: Unable to encrypt request >> ", e);
        }
        return null;
    }

    public static String decryptAES(String encryptedRequest, String secretkey, String iv) {
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
        SecretKeySpec keyspec = new SecretKeySpec(secretkey.getBytes(), AES);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            return new String(cipher.doFinal(EncyptionUtil.hexToBytes(encryptedRequest)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            LOG.error("ERROR: Unable to decrypt request >> ", e);
        }
        return null;
    }

    public Object validateHeaderValue(HttpServletRequest servletRequest, String serviceName){

        //get The Header values
        String authorizationValue = servletRequest.getHeader(EncryptionHeader.AUTHORIZATION.getName());
        String signatureValue = servletRequest.getHeader(EncryptionHeader.SIGNATURE.getName());
        String signatureMethValue = servletRequest.getHeader(EncryptionHeader.SIGNATURE_METH.getName());

        LOG.info("authorizationValue => {}", authorizationValue);
        LOG.info("signatureValue => {}", signatureValue);
        LOG.info("signatureMethodValue => {}", signatureMethValue);

        Object credentialOrErrorCode = validateAuthenticationValue(authorizationValue, serviceName);
        if (credentialOrErrorCode instanceof String){
            LOG.error("Error validating AuthenticationValue => {}",credentialOrErrorCode);
            return String.valueOf(credentialOrErrorCode);
        }

       // InstitutionCredentials credentials = InstitutionCredentials.class.cast(credentialOrErrorCode);

        /*String validationResult = validateSignatureValue(signatureValue,credentials,password);
        if (validationResult != null){
            LOG.error("Error validating SignatureValue => {}",validationResult);
            return validationResult;
        }*/

        if(!validateSignatureMethValue(signatureMethValue)){
            LOG.error("SignatureMethValue is NOT sha256");
            return Response.INVALID_DATA_PROVIDED.getCode();
        }

        //return credentials
       Map<String, String> mapCredentials = new HashMap<>();
        /* mapCredentials.put(Constants.SECRET_KEY, credentials.getAesKey());
        mapCredentials.put(Constants.IV, credentials.getIvSpec());
        mapCredentials.put(Constants.USERNAME,credentials.getInstitutions().getCode());*/
        return mapCredentials;

        //we then decrypt the data provided using the credentials details
        //Finally we then encrypt the data and respond
    }

    private Object validateOrganisationCodeAndGetCredential(String organisationCode, String serviceName){

        if (organisationCode == null || organisationCode.isEmpty() || "".equals(organisationCode)){
            return Response.INVALID_DATA_PROVIDED.getCode();
        }

        //decode organisationCode
        String decodedOrganisationCode;
        try {
            decodedOrganisationCode = new String(Base64.getDecoder().decode(organisationCode));
        } catch (Exception e) {
            LOG.error("Unable to organisationCode decode ==> {}", organisationCode, e);
            return Response.INVALID_DATA_PROVIDED.getCode();
        }

      /*  Optional<InstitutionCredentials> credentials = institutionCredentialsRepo.getInstitutionCredentialsByInstitutionsAndService(decodedOrganisationCode, serviceName);
        if (!credentials.isPresent()){
            return Response.INVALID_LOGIN_CREDENTIALS.getCode();
        }

        InstitutionCredentials institutionCredentials = credentials.get();
        return institutionCredentials;*/
        return null;
    }

    private Object validateAuthenticationValue(String authorizationValue, String serviceName){

        //using the username, we decode the authentication value and split to get the password.(username:password).replace(username:, "")
        //validate AuthorisationValue
        if (authorizationValue == null || authorizationValue.isEmpty() || "".equals(authorizationValue)){
            return Response.INVALID_DATA_PROVIDED.getCode();
        }
        //decode the auth.
        String decodedAuthValue;
        try {
            decodedAuthValue = new String(Base64.getDecoder().decode(authorizationValue));
            LOG.info("The decodedAuthValue is {}", decodedAuthValue);
        } catch (Exception e) {
            LOG.error("Unable to decode Authentication value => {}", authorizationValue, e);
            return Response.INVALID_DATA_PROVIDED.getCode();
        }

        //Split username:password with ":";
        String[] split = decodedAuthValue.split(FULL_COLON, 2);
        if (split == null || split.length < 2){
            LOG.error("Invalid AuthValue derived After splitting using : , ==> {}", Arrays.toString(split));
            return Response.INVALID_DATA_PROVIDED.getCode();
        }

        String organisationCode = split[0];//organisationCode/institutionCode
        password = split[1];//password

        //get credential using organisationCode and password
       /* Optional<InstitutionCredentials> institutionCredentials =
                institutionCredentialsRepo.getInstitutionCredentialsByInstitutionsAndService(organisationCode, serviceName);

        if (!institutionCredentials.isPresent()){
            LOG.info("Credential not found using organisationCode : {} and service : {}", organisationCode, serviceName);
            return Response.INVALID_LOGIN_CREDENTIALS.getCode();
        }

        InstitutionCredentials credentials = institutionCredentials.get();

        //hash the password and compare with the derived password.
        String hashPassword = PasswordUtil.hashPassword(password, credentials.getInstitutions().getCode(), credentials.getIvSpec());
        if (!credentials.getPassword().equals(hashPassword)){
            return Response.INVALID_LOGIN_CREDENTIALS.getCode();
        }
*/
        return null;
    }

/*    public static void main(String[] arg){
        System.out.println(new String(Base64.getEncoder().encode("AGTMGR00002".getBytes())));
        System.out.println(" Authorization => "+new String(Base64.getEncoder().encode("AGTMGR00002:0-}~X[zLC9(rZy`F".getBytes())));
        String format = new SimpleDateFormat("yyyyMMdd").format(new Date());
        System.out.println(format);

        System.out.println("SIGNATURE => "+EncyptionUtil.generateSha256("AGTMGR00002"+format+"0-}~X[zLC9(rZy`F"));

        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("Number","KTU43BG");
//        jsonObject.addProperty("PhoneNumber","07069606863");
//        jsonObject.addProperty("Type","VEHICLE_REGISTRATION_NUMBER");

        jsonObject.addProperty("Number","BDG72EQ");
        jsonObject.addProperty("PhoneNumber","08034067144");
        jsonObject.addProperty("Type","VEHICLE_REGISTRATION_NUMBER");

        //encrypt
        System.out.println(jsonObject);
        System.out.println("data => "+encryptAES(jsonObject.toString(), "ylAmwFRhz34WnLgp", "b/xMCwMSxXIgUkA2"));

        String encryptedErrorString = "af4e31150953c0dab2b3c4da3c96960972dd77029fb641f8d9218d97035ebe8e66b48f7834ffd05b053c039eab0d5e9bb35ba5dc48bafb03d4e31d7b47a4734e32ad777c9078ddf483b205d0a3f25ca854f38ce3cc2dee69f7e7213a4c0344b9579dec9e097134d3e506a3ce7c2c3c25498a321c637198df50a29147a55553330089a85380a6593447eb9ab0a3f0ef6cea2d72438093961824f77ae363143799265429014adf79bd9837651f875aa9dbd7febf26276f36e658be1c2b6c2d1d4a4bc659559dbf84c1d1b669a7dcbd677202f3390cb627a31d6ace74aafc8bfa5f";
        System.out.println(decryptAES(encryptedErrorString,"cx4HxLPU8ZnQ+enp", "3kXlStGyRgsY2el8"));

    }*/

   /* private String validateSignatureValue(String signatureValue, InstitutionCredentials credentials, String password){

        //confirm that the sha256 of the signatureValue is the same as sha256 of (usernameyyyyMMddpassword)
        if (signatureValue == null || signatureValue.isEmpty() || "".equals(signatureValue)){
            return Response.INVALID_DATA_PROVIDED.getCode();
        }
        //generate the date in the format as string
        String format = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String formattedDate = format;
        //concatenate username(organisationCode/InstitutionCode), date and password according to format
        String concatenatedString = credentials.getInstitutions().getCode() + formattedDate + password;
        //generate the sha256 value of the concatenated value
        String sha256OfConcatenatedString = EncyptionUtil.generateSha256(concatenatedString);
        // compare the values
        if (!signatureValue.equals(sha256OfConcatenatedString)){
            return Response.INVALID_LOGIN_CREDENTIALS.getCode();
        }

        return null;
    }*/

    private boolean validateSignatureMethValue(String signatureMeth){

        //confirm that signature meth is 256
        //confirm that the signature meth is the expected sha256
        if (signatureMeth == null || signatureMeth.isEmpty() || "".equals(signatureMeth)){
            return false;
        }

        if (signatureMeth.equalsIgnoreCase("sha256")){
            return true;
        }

        return false;
    }

}
