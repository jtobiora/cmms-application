package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;


@Data
@NoArgsConstructor
public class TabularReportByReportType implements Serializable {

    private BigInteger totalVolume;
    private BigDecimal totalValue;
    private Long id;
    private String name;
    private String code;

    public TabularReportByReportType(BigInteger volume, BigDecimal value, Long id, String name, String code) {
        this.totalVolume = volume == null ? BigInteger.ZERO : volume;
        this.totalValue = value == null ? BigDecimal.ZERO : value;
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
