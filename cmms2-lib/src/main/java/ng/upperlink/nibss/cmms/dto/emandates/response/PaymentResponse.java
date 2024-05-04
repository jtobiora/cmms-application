package ng.upperlink.nibss.cmms.dto.emandates.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    String amount;
    String batchId;
    String mandateCode;
    String narration;
    String responseCode;
    String subscriberCode;
}
