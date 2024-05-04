package ng.upperlink.nibss.cmms.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiarySearchRequest {
    private String beneficiaryName;
    private String accountName;
    private String accountNumber;
    private String activated;
    private String bankCode;
}

