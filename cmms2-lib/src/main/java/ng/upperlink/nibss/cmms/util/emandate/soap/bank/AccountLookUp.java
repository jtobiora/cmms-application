package ng.upperlink.nibss.cmms.util.emandate.soap.bank;


import ng.upperlink.nibss.cmms.dto.bank.AccountLookUpRequest;
import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapBuilder;
import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapPayload;
import ng.upperlink.nibss.cmms.util.emandate.soap.transformer.soap.AccountValidationTransformer;
import ng.upperlink.nibss.cmms.util.emandate.soap.xml.accountvalidation.GetAccountDetailsResponse;
import ng.upperlink.nibss.cmms.util.encryption.EncyptionUtil;
import org.springframework.http.ResponseEntity;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AccountLookUp extends XMLBuilder {
    private static GetAccountDetailsResponse accountDetailsResponse;
    private static final String ENDPOINT = "http://css.ng/v1prod/name_enquiry";
    static final char[] CHARSET_AZ_09 = "0123456789".toCharArray();

//    public static void lookupOld(int bankCode, int accountNumber) {
//        try {
//            String accountLookUpRequest = setUpAccountRequest(bankCode,accountNumber);
//            String soapAction = "urn:css_name_enquiry_webservice#doNameEnquiry";
//
//            Map<String, String> namespace = new HashMap<String, String>() {{
//                put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//                put("xsd", "http://www.w3.org/2001/XMLSchema");
//                put("urn", "urn:css_name_enquiry_webservice");
//            }};
//
//            SoapBuilder soapBuilder = new SoapBuilder("soapenv", namespace);
//            soapBuilder.set("soapenv__Header", new SoapPayload());
//            soapBuilder.set("soapenv__Body", new SoapPayload());
//            soapBuilder.get("soapenv__Body").set("urn__doNameEnquiry", new SoapPayload());
//            String nameEnquireRequestXML = getNameEnquireRequestXML(accountLookUpRequest);
//            soapBuilder.get("soapenv__Body").get("urn__doNameEnquiry").set("input_xml", new SoapPayload(nameEnquireRequestXML));
//            System.out.println(nameEnquireRequestXML);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public static void lookup(String bankCode, String accountNumber) {
        try {
            String inputXMLrequest = setUpAccountRequest(bankCode, accountNumber);
            String soapAction = "urn:css_name_enquiry_webservice#doNameEnquiry";

            Map<String, String> namespace = new HashMap<String, String>() {{
                put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                put("xsd", "http://www.w3.org/2001/XMLSchema");
                put("urn", "urn:css_name_enquiry_webservice");
            }};

            SoapBuilder soapBuilder = new SoapBuilder("soapenv", namespace);
            soapBuilder.set("soapenv__Header", new SoapPayload());
            soapBuilder.set("soapenv__Body", new SoapPayload());
            soapBuilder.get("soapenv__Body").set("urn__doNameEnquiry", new SoapPayload("", new HashMap<String, String>() {{
                put("soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
            }}));
            soapBuilder.get("soapenv__Body").get("urn__doNameEnquiry").set("input_xml", new SoapPayload(inputXMLrequest, new HashMap<String, String>() {{
                put("xsi:type", "xsd:string");
            }}));

            NameEnquiryWebService nameEnquiryWebService = new NameEnquiryWebService(ENDPOINT, soapAction, soapBuilder.getXml());
            ResponseEntity response = nameEnquiryWebService.getResponse();
            String xml = Objects.requireNonNull(response.getBody()).toString();
            System.out.println(">>>>>>>>>>>>>"+xml);
            System.out.println(response);


            // Casting the XML string to a Class
            AccountValidationTransformer<GetAccountDetailsResponse> accountValidationTransformer = new AccountValidationTransformer<>();
            accountDetailsResponse = accountValidationTransformer.transform(xml, GetAccountDetailsResponse.class);
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+accountDetailsResponse.getAccountDetailsResult());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }
    public static String setUpAccountRequest(String bankCode,String accountNumber )
    {
         final String secreteKey = "utjjT5dX7TG37D9TwTAE8NccamqwNZWNJaVzPt4b";
         final String clientId = "WSABNSLCNC";
               String enqId = randomString(CHARSET_AZ_09,8);
               String salt = randomString(CHARSET_AZ_09, 20);
               String mac = EncyptionUtil.doSHA512Encryption(clientId+"-"+secreteKey+"-"+salt);
        AccountLookUpRequest accountLookUpRequest = new AccountLookUpRequest(clientId, bankCode, accountNumber, enqId, salt, mac);
        return XMLBuilder.getNameEnquireRequestXML(accountLookUpRequest);
    }
    public static void main(String[] agrs)
    {
        lookup("058","0231116887");
    }
}
