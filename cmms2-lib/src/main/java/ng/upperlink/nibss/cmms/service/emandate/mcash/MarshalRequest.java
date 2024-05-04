package ng.upperlink.nibss.cmms.service.emandate.mcash;
import ng.upperlink.nibss.cmms.dto.emandates.AuthenticationRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static java.lang.Boolean.TRUE;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;


public class MarshalRequest {
  private static JAXBContext jaxbContext;

  static {
    try {
      jaxbContext = JAXBContext.newInstance(AuthenticationRequest.class);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  public static String marshalRequest(AuthenticationRequest request) throws IOException, JAXBException {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {

      final Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
      marshaller.marshal(request, output);

      return output.toString();
    }
  }

//  public static void main(String[] arg) throws Exception {
//    final AuthenticationRequest request = new AuthenticationRequest();
//    request.setAccountName("Henry Odinaka");
//    request.setAccountNumber("000111111");
//    request.setAdditionalRequiredData("1111");
//    request.setSessionId("000001100913103301000000000001");
//    request.setRequestorId("0000000001");
//    request.setPayerPhoneNumber("08060000000");
//    request.setMandateReferenceNumber("1234/USSD/00222");
//    request.setProductCode("USSD");
//    request.setAmount(new BigDecimal(200).setScale(2));
//    request.setFiiInstitution("001001");
//    request.setPasscode("A23983838838FEDAA23983838838FEDA");
//
//    System.out.println(marshalRequest(request));
//  }

}