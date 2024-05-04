package ng.upperlink.nibss.cmms.dto.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLookUpRequest {
    private String clientId;//const
    private String bankCode;
    private String accountNumber;
    private String enquiryId;//generated
    private String salt;//generated
    private String mac;//hashed generated clientId+"-"+secreteKey+"-"+salt
}
