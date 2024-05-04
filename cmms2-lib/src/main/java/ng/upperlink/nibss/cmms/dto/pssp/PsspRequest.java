package ng.upperlink.nibss.cmms.dto.pssp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsspRequest {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message ="Account number required"  )
    @Size(min = 10, max = 10, message = "Account number should be {max} digits")
    private String accountNumber;

    @NotNull(message = "Account name required")
    private String accountName;

    @NotNull(message = "Pssp name cannot be null")
    private String psspName;

    @NotNull(message = "Bvn not provided")
    @Size(min = 11, max = 11, message = "BVN should be {max} digits")
    private String bvn;

    @NotNull(message = "Bank code can not be null")
    private String bankCode;

    @NotNull(message = "Please select industry")
    private Long industryId;

    @JsonIgnore
    private Industry industry;

    private String description;

    @NotNull(message = "Rc number required")
    private String rcNumber;
}

