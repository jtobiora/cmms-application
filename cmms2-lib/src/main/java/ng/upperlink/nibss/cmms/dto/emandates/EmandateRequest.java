package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmandateRequest implements Serializable{
    private Long id;

    @NotBlank(message = "Account number is required")
    private String accountNumber;
//    @JsonIgnore
//    @NotBlank(message = "Account name is required")
//    private String accountName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "payer name is required")
    private String payerName;

    @NotBlank(message = "Payer address is required")
    private String payerAddress;

    @NotBlank(message = "Mandate narration is required")
    private String narration;

    @NotNull(message = "Product is required")
    private Long productId;

    @NotBlank(message = "Email address is required")
    @Email(message = "invalid emailAddress address")
    private String emailAddress;

    @NotBlank(message = "Please provide a start date")
    private String startDate;

    @NotBlank(message = "Please provide a end date")
    private String endDate;

    @NotBlank(message = "Please provide the channel")
    private String channelCode;

    @NotBlank(message = "Required pass code from Financial Institution")
    private String subscriberPassCode;

    @NotBlank(message = "Subscriber's code is required")
    private String subscriberCode;

    @NotBlank(message = "Subscriber's bank code is required")
    private String bankCode;

    private boolean fixedAmountMandate;
    @NotNull(message = "Amount is required")

    @NotBlank(message = "amount required")
    private BigDecimal amount;

    @NotNull(message = "Frequency must be provided!")
    private Integer frequency;

//    @NotBlank(message = "Biller is required")
//    @NotNull(message = "Please select a biller")
//    private String biller;

    //private String validityDateRange;
    //private boolean fixedAmountMandate;

    //subscriber's bank
//    @NotBlank(message = "Bank Code is required")
//
//
//
//    private String uploadImage;
}
