package ng.upperlink.nibss.cmms.dto.mandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelMandateRequest {

    private String mandateCode;
    private String subscriberMandateCode;
    private String accountNumber;
    private String payerName;
    private String accountName;
    private BigDecimal amount;
    private String narration;
    private String phoneNumber;
    private String mandateFile;
    private String email;
    private String address;
    private int frequency;
    private boolean mandateType;


}

