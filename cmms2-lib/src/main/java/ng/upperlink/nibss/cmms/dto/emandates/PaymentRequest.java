package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PaymentRequest {


    private AuthParam auth;
    @NotNull(message = "Subscriber's code is required")
    String subscriberCode;

    @NotNull(message = "Amount is required")
    BigDecimal amount;

    @NotNull(message = "Mandate code is required")
    String mandateCode;
}
