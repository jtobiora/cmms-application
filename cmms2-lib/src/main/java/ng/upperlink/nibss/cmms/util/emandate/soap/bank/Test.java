package ng.upperlink.nibss.cmms.util.emandate.soap.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.upperlink.nibss.cmms.util.emandate.JsonBuilder;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;

public class Test {

    protected static final String SOAP_RESPONSE_STRING = "<SOAP-ENV:Body><ns1:doNameEnquiryResponse><return xsi:type=\"xsd:string\">&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&#13;\n" +
            "&lt;NameEnquiryResponse&gt;&#13;\n" +
            "    &lt;Header&gt;&#13;\n" +
            "        &lt;EnquiryId&gt;57969360&lt;/EnquiryId&gt;&#13;\n" +
            "        &lt;ClientId&gt;WSABNSLCNC&lt;/ClientId&gt;&#13;\n" +
            "    &lt;/Header&gt;&#13;\n" +
            "    &lt;AccountRecord&gt;&#13;\n" +
            "        &lt;Status&gt;98&lt;/Status&gt;&#13;\n" +
            "        &lt;BankCode&gt;058&lt;/BankCode&gt;&#13;\n" +
            "        &lt;Surname&gt;&lt;/Surname&gt;&#13;\n" +
            "        &lt;OtherNames&gt;&lt;/OtherNames&gt;&#13;\n" +
            "        &lt;AccountNumber&gt;0231116887&lt;/AccountNumber&gt;&#13;\n" +
            "    &lt;/AccountRecord&gt;&#13;\n" +
            "    &lt;HashValue&gt;&lt;/HashValue&gt;&#13;\n" +
            "&lt;/NameEnquiryResponse&gt;</return></ns1:doNameEnquiryResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

    protected static final String ENVELOPE_START_STRING = "<SOAP-ENV:Body><ns1:doNameEnquiryResponse><return xsi:type=\"xsd:string\"><?xml version=\"1.0\" encoding=\"UTF-8\"?>&#13;";
    protected static final String RETURN_STRING = "</return></ns1:doNameEnquiryResponse>";
    protected static final String ENVELOPE_END_STRING = "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    protected static final String CHAR_STRING = "&#13;";
    protected static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";


    public static void main(String[] args) throws IOException {

        String jsonData = XMLToJson();
        ObjectMapper objectMapper = new ObjectMapper();
        Response response = objectMapper.readValue(jsonData, Response.class);
        System.out.println(response.getStatus());

    }

    public static String XMLToJson() {
        String strippedString = SOAP_RESPONSE_STRING.replaceAll("&lt;", "<").replaceAll("&gt;", ">");

        strippedString = strippedString.replace(ENVELOPE_START_STRING, "")
                .replace(RETURN_STRING, "").replace(ENVELOPE_END_STRING, "").replaceAll(CHAR_STRING, "");

        final String xmlString = new StringBuilder(XML_HEADER).append(strippedString).toString();
        JSONObject jsonObject = XML.toJSONObject(xmlString);
        JSONObject nameEnquiryResponse = jsonObject.getJSONObject("NameEnquiryResponse");
        JSONObject accountRecord = nameEnquiryResponse.getJSONObject("AccountRecord");
        return accountRecord.toString();
    }

}