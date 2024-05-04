package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;

@Data
public class PaymentStatusRequest {
    private AuthParam auth;
    String mandateCode;
}
