package ng.upperlink.nibss.cmms.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MandateReport {
    private List<MandateDashboard> mandateReport;
}
