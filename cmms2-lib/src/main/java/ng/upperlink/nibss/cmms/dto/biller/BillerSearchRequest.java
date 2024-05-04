package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillerSearchRequest {
    private String accountNumber;
    private String accountName;
    private String code;
    private String companyName;
    private String description;
    private String rcNumber;
    private String activated;
    private String bvn;
}
