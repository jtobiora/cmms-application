package ng.upperlink.nibss.cmms.dto.emandates;

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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationRequest {

    @XmlElement(name = "SessionID")
    private String sessionId;

    @XmlElement(name = "RequestorID")
    private String requestorID;

    @XmlElement(name = "PayerPhoneNumber")
    private String payerPhoneNumber;

    @XmlElement(name = "Amount")
    private BigDecimal amount;

    @XmlElement(name = "AdditionalFIRequiredData")
    private String additionalFIRequiredData;

    @XmlElement(name = "FIInstitution")
    private String fIInstitution;

    @XmlElement(name = "AccountNumber")
    private String accountNumber;

    @XmlElement(name = "AccountName")
    private String accountName;

    @XmlElement(name = "PassCode")
    private String passCode;

    @XmlElement(name = "MandateReferenceNumber")
    private String mandateReferenceNumber;

    @XmlElement(name = "ProductCode")
    private String productCode;


}
