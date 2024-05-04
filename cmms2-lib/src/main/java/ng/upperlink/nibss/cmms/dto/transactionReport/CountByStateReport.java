package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CountByStateReport {

    private String stateName;
    private long count;

    public CountByStateReport(String stateName, long count) {
        this.stateName = stateName;
        this.count = count;
    }
}
