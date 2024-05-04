package ng.upperlink.nibss.cmms.util.emandate;

import ng.upperlink.nibss.cmms.dto.emandates.AuthenticationRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ObjectXmlConvertion {
    public static String objectToXml(AuthenticationRequest mcashAuthObj) throws IOException,JAXBException
    {
        JAXBContext jaxbContext = null;
        Marshaller marshallerObj = null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            jaxbContext = JAXBContext.newInstance(AuthenticationRequest.class);
            marshallerObj = jaxbContext.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(mcashAuthObj,outputStream);
            return outputStream.toString();
        }
    }
    public static  void  xmlToObject()
    {

    }
}
