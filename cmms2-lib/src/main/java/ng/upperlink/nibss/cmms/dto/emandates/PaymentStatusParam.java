package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusParam {
    private String requstDate;
    private String mandateCode;
    private String valueDate;
    private String batchId;
    private String transactionId;
    private String status;
}
