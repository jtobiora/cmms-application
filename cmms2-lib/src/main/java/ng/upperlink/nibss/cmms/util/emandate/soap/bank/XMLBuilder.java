package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.bank.AccountLookUpRequest;
import ng.upperlink.nibss.cmms.dto.emandates.AuthenticationRequest;
import ng.upperlink.nibss.cmms.dto.emandates.response.AuthenticationResponse;
import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapBuilder;
import ng.upperlink.nibss.cmms.util.emandate.soap.builder.SoapPayload;
import org.json.JSONObject;
import org.json.XML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
@Slf4j
public class XMLBuilder {
    public static String getNameEnquireRequestXML(AccountLookUpRequest request) {

        try{
            SoapBuilder soapBuilder = new SoapBuilder("");
            soapBuilder.setAsXml();
            soapBuilder.set("NameEnquiryRequest", new SoapPayload());
            soapBuilder.get("NameEnquiryRequest").set("Header", new SoapPayload());
            soapBuilder.get("NameEnquiryRequest").get("Header").set("EnquiryId", new SoapPayload(request.getEnquiryId()));
            soapBuilder.get("NameEnquiryRequest").get("Header").set("ClientId", new SoapPayload(request.getClientId()));
            soapBuilder.get("NameEnquiryRequest").get("Header").set("Salt", new SoapPayload(request.getSalt()));
            soapBuilder.get("NameEnquiryRequest").get("Header").set("Mac", new SoapPayload(request.getMac()));
            soapBuilder.get("NameEnquiryRequest").set("AccountRecord", new SoapPayload());
            soapBuilder.get("NameEnquiryRequest").get("AccountRecord").set("BankCode", new SoapPayload(request.getBankCode()));
            soapBuilder.get("NameEnquiryRequest").get("AccountRecord").set("AccountNumber", new SoapPayload(request.getAccountNumber()));

            return soapBuilder.getXml();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String mcashAuthRequetXML(AuthenticationRequest request) {

        try{
            SoapBuilder soapBuilder = new SoapBuilder("");
            soapBuilder.setAsXml();
            soapBuilder.set("AuthenticationRequest", new SoapPayload());
            soapBuilder.get("AuthenticationRequest").set("SessionID",new SoapPayload(request.getSessionId()));
            soapBuilder.get("AuthenticationRequest").set("RequestorID",new SoapPayload(request.getRequestorID()));
            soapBuilder.get("AuthenticationRequest").set("PayerPhoneNumber",new SoapPayload(request.getPayerPhoneNumber()));
            soapBuilder.get("AuthenticationRequest").set("MandateReferenceNumber", new SoapPayload(request.getMandateReferenceNumber()));
            soapBuilder.get("AuthenticationRequest").set("ProductCode", new SoapPayload(request.getProductCode()));
            soapBuilder.get("AuthenticationRequest").set("Amount", new SoapPayload(request.getAmount()));
            soapBuilder.get("AuthenticationRequest").set("AdditionalFIRequiredData", new SoapPayload(request.getAdditionalFIRequiredData()));
            soapBuilder.get("AuthenticationRequest").set("FIInstitution", new SoapPayload(request.getFIInstitution()));
            soapBuilder.get("AuthenticationRequest").set("AccountNumber", new SoapPayload(request.getAccountNumber()));
            soapBuilder.get("AuthenticationRequest").set("AccountName", new SoapPayload(request.getAccountName()));
            soapBuilder.get("AuthenticationRequest").set("PassCode", new SoapPayload(request.getPassCode()));

            return soapBuilder.getXml();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static   <T> String marshal(T object, Class<T> clazz) {
        StringWriter writer = new StringWriter();

        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            jaxbMarshaller.marshal(object, writer);
            String xmlResponse = writer.toString();
            return xmlResponse;
        } catch(JAXBException ex) {
            log.error("Marshaller Exception: ", ex);
            ex.printStackTrace();
            return null;
        }
    }
    public static void xmlToObject(String xml)
    {
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(AuthenticationResponse.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            AuthenticationResponse response = (AuthenticationResponse) jaxbUnmarshaller.unmarshal(new StringReader(xml));

            System.out.println(response);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }

    public static <T> T unmarshalWithoutDecrypting(String object, Class<T> clazz) {
        StringReader reader = new StringReader(object);

        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return clazz.cast(jaxbUnmarshaller.unmarshal(reader));
        } catch(JAXBException ex) {
            return null;
        }
    }
    public static void main (String[]args)
    {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                " <AuthenticationResponse> " +
                " <SessionID>999999700101010000CAC010045820</SessionID> " +
                " <RequestorID>999058</RequestorID> " +
                " <PayerPhoneNumber>08062864121</PayerPhoneNumber>  " +
                " <MandateReferenceNumber>CAC01/004/58200837</MandateReferenceNumber> " +
                " <ProductCode>USSD</ProductCode>  " +
                " <Amount>100.0</Amount> " +
                " <FIInstitution>999058</FIInstitution> " +
                " <AccountNumber>0231116887</AccountNumber>  " +
                " <AccountName>Odinaka Henry Onah</AccountName> " +
                " <ResponseCode>63</ResponseCode> " +
                " </AuthenticationResponse>";
//        xmlToObject(xml);
        AuthenticationResponse authenticationResponse = unmarshalWithoutDecrypting(xml, AuthenticationResponse.class);
        System.out.println(authenticationResponse);

//        AuthenticationRequest authenticationRequest =new AuthenticationRequest("398498","req","98939804","ouiuhbf","jdkjkjkf",new BigDecimal(900),"jfkjhfjf","jfhjfjjfhf","jhdjajfjha","jkkdjjdkf","jdjdkjd");
//        String xml = mcashAuthRequetXML(authenticationRequest);
//        String xml = marshal(authenticationRequest);
//        System.out.println(xml);
    }
}
