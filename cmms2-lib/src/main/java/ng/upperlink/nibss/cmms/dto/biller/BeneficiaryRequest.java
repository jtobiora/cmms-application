package ng.upperlink.nibss.cmms.dto.biller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryRequest implements Serializable{

    private Long beneficiaryId;

    @NotNull(message="Please provide the beneficiary name!")
    @NotBlank(message="Name cannot be empty!")
    private String beneficiaryName;

    @NotNull(message="Please provide the account number!")
    @NotBlank(message="Account number cannot be empty!")
    private String accountNumber;

    @NotNull(message="Please provide the account name!")
    @NotBlank(message="Account name cannot be empty!")
    private String accountName;

    @NotNull(message="Please provide the beneficiary's bank!")
    @NotBlank(message="Beneficiary bank cannot be empty!")
    private String bankCode;

    private boolean activated;
}
