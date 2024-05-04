package ng.upperlink.nibss.cmms.dto.pssp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsspSearchRequest {
    private String accountNumber;
    private String accountName;
    private String psspCode;
    private String psspName;
    private String activated;
    private String bvn;
    private String bankCode;
}
