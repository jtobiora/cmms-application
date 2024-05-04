package ng.upperlink.nibss.cmms.dto.mandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MandateSearchRequest {
    private String mandateCode;
    private String mandateStartDate;
    private String mandateEndDate;
    private String mandateStatus;
    private String subscriberCode;
    private String accName;
    private String accNumber;
    private String bvn;
    private String email;
    private String bankCode;
    private String productName;
    private String mandateType;
    private String mandateCategory;
    private String channel;
    private String address;
    private String payerName;
    private String amount;
    private String frequency;
}
