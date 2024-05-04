package ng.upperlink.nibss.cmms.dashboard;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SuperAdminDashboardDto extends DashboardDto {

    private long successfulEnrollmentCount;
    private long failedEnrollmentCount;

    private long pendingEnrollmentCount;

    private long branchCount;

    private long agentCount;

    private long institutionCount;

    private long agentManagerCount;

    private long enrollerCount;

    private long nibssUserCount;

    private List countryStatistic;



}
