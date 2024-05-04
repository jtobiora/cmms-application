package ng.upperlink.nibss.cmms.dto.mandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MandateRequest implements Serializable{
    private Long id;

    @NotBlank(message = "Email address is required")
    @Email(message = "invalid emailAddress address")
    private String email;

    private String billerSubscriberRef;

    private String payerName;

    private BigDecimal amount;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Biller is required")
    private String biller;

    @NotBlank(message = "Product cannot be empty")
    private String product;

    //private String validityDateRange;

    @NotBlank(message = "Please provide a start date")
    private String mandateStartDate;

    @NotBlank(message = "Please provide an end date")
    private String mandateEndDate;

    //private boolean fixedAmountMandate;

    private String payerAddress;

    @NotNull(message = "Frequency must be provided!")
    private Integer frequency;

    //subscriber's bank
    @NotBlank(message = "Bank Code is required")
    private String bankCode;

    @NotEmpty(message = "Account Number is required")
    @Size(min = 10, max = 10, message = "Account number must be {max} digits")
    private String accountNumber;

    private String accountName;

    private String narration;

    private boolean fixedAmountMandate;

    private String uploadImage;

    private String bvn;

    private BigDecimal variableAmount;
}
