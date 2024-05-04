package ng.upperlink.nibss.cmms.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillerDashboard {
    private Long totalBillers;
    private Long activeBillers;
    private Long inactiveBillers;

    public BillerDashboard(Long totalBillers, Long activeBillers, Long inactiveBillers) {
        this.totalBillers = totalBillers;
        this.activeBillers = activeBillers;
        this.inactiveBillers = inactiveBillers;
    }
}
