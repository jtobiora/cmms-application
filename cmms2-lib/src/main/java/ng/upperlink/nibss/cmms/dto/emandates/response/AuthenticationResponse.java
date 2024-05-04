package ng.upperlink.nibss.cmms.dto.emandates.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "AuthenticationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationResponse {

    @XmlElement(name = "SessionID")
    private String SessionID;

    @XmlElement(name = "RequestorID")
    private String RequestorID;

    @XmlElement(name = "PayerPhoneNumber")
    private String PayerPhoneNumber;

    @XmlElement(name = "MandateReferenceNumber")
    private String MandateReferenceNumber;

    @XmlElement(name = "ProductCode")
    private String ProductCode;

    @XmlElement(name = "Amount")
    private BigDecimal Amount;

    @XmlElement(name = "FIInstitution")
    private String FIInstitution;

    @XmlElement(name = "AccountNumber")
    private String AccountNumber;

    @XmlElement(name = "AccountName")
    private String AccountName;

    @XmlElement(name = "ResponseCode")
    private String ResponseCode;
}
