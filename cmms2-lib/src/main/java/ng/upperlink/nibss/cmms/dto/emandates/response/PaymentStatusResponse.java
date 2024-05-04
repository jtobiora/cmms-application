package ng.upperlink.nibss.cmms.dto.emandates.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ng.upperlink.nibss.cmms.dto.emandates.PaymentStatusParam;

@Data
@AllArgsConstructor
public class PaymentStatusResponse {
    private String mandateCode;
    private PaymentStatusParam param;
}
