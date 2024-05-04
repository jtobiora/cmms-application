package ng.upperlink.nibss.cmms.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentDashboardDto extends DashboardDto {

    private long enrollerCount;

    private  long branchCount;
}
