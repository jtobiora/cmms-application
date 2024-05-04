package ng.upperlink.nibss.cmms.dto.biller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesRequest implements Serializable{

    private Long billerId;

    private BigDecimal markUpFee;

    private BigDecimal percentageAmount;

    private String splitType;

    private String feeBearer;

    private boolean isDebitAtTransactionTime;

    private String billerDebitAccountNumber;

    private String bankCode;

    private String accountNumber;

    private String accountName;

    private boolean markUpFeeSelected;
}

