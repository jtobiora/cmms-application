package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeSearchRequest {
    private String markUpFee;
    private String splitType;
    private String feeBearer;
    private String billerAccNumber;
    private String billerName;
    private String debitAtTrans;
    private String markUpFeeSelected;
    private String beneficiaryBankCode;
}
