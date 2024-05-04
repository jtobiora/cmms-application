package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CountByStateAndLgaReport extends CountByStateReport {

    private String lgaName;

    public CountByStateAndLgaReport(String stateName, String lgaName, long count) {
        super(stateName, count);
        this.lgaName = lgaName;
    }
}
