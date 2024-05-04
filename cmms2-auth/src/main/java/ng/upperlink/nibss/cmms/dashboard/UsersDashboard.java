package ng.upperlink.nibss.cmms.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsersDashboard {
    private Long totalNibssUsers;

    private Long totalBankUsers;

    private Long totalBillerUsers;

    private Long totalPsspUsers;

    public UsersDashboard(Long totalNibssUsers, Long totalBankUsers, Long totalBillerUsers, Long totalPsspUsers) {
        this.totalNibssUsers = totalNibssUsers;
        this.totalBankUsers = totalBankUsers;
        this.totalBillerUsers = totalBillerUsers;
        this.totalPsspUsers = totalPsspUsers;
    }
}
