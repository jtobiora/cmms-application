package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeResponse {
    private Long id;

    private Biller biller;

    private BigDecimal fixedAmount;

    private BigDecimal percentageAmount;

    private String splitType;

    private String feeBearer;

    private boolean isDebitAtTransactionTime;

    private String billerDebitAccountNumber;
}