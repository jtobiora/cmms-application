package ng.upperlink.nibss.cmms.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgentManagerDashboardDto  extends DashboardDto {

    private long agentCount;
    private long enrollerCount;

    private long branchCount;


    private List countryStatistic;


}
