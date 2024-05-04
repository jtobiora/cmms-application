package ng.upperlink.nibss.cmms.dto.biller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillerRequest extends CompanyRequest {

	private static final long serialVersionUID = 1L;
//	@NotNull(message ="Account number required"  )
//    @Size(min = 10, max = 10, message = "Account number should be {max} digits")
    private String accountNumber;

//    @NotNull(message = "Account name required")
    //@Size(min = 5,message = "! Account name requires at least {min} characters")
    private String accountName;

    @NotNull(message = "Rc number required")
    //@Size(min = 5, max = 5, message = "Rc number must be {max} digits")
    private String rcNumber;

    private String username;

//    @NotBlank(message = "Domain name cannot be null")
    private String domainName;

//	@NotNull(message = "Bvn not provided")
//    @Size(min = 11, max = 11, message = "BVN should be {max} digits")
    private String bvn;

//	@NotNull(message = "Bank is required")
    private Long bankId;
//    @JsonIgnore
//    private Bank bank;

    private boolean isSelfBiller;

    public BillerRequest (CompanyRequest companyRequest){
    this.setId(companyRequest.getId());
    this.setName(companyRequest.getName());
//    this.setIndustry(companyRequest.getIndustry());
    this.setDescription(companyRequest.getDescription());
}
}
