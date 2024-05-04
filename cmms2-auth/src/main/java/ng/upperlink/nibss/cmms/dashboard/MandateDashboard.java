package ng.upperlink.nibss.cmms.dashboard;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MandateDashboard {
    @JsonView(MandateView.Summary.class)
    private Long totalMandates;

    @JsonView(MandateView.Summary.class)
    private Long approvedMandates;

    @JsonView(MandateView.Summary.class)
    private Long pendingMandates;

    @JsonView(MandateView.Summary.class)
    private Long rejectedMandates;

    @JsonView(MandateView.Summary.class)
    private String startDate;

    @JsonView(MandateView.Summary.class)
    private String endDate;

    @JsonView(MandateView.BankDetail.class)
    private String bankName;

    @JsonView(MandateView.SubscriberDetail.class)
    private String subscriber;

    @JsonView(MandateView.BillerDetail.class)
    private String billerName;

    public MandateDashboard(String startDate,String endDate, Long totalMandates, Long approvedMandates, Long pendingMandates, Long rejectedMandates){
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalMandates = totalMandates;
        this.approvedMandates = approvedMandates;
        this.pendingMandates = pendingMandates;
        this.rejectedMandates = rejectedMandates;
    }

    public MandateDashboard(String startDate, String endDate, String bankName,Long totalMandates, Long approvedMandates, Long pendingMandates, Long rejectedMandates){
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalMandates = totalMandates;
        this.approvedMandates = approvedMandates;
        this.pendingMandates = pendingMandates;
        this.rejectedMandates = rejectedMandates;
        this.bankName = bankName;
    }

    public MandateDashboard(String startDate, String endDate, String bankName,String billerName,Long totalMandates, Long approvedMandates, Long pendingMandates, Long rejectedMandates){
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalMandates = totalMandates;
        this.approvedMandates = approvedMandates;
        this.pendingMandates = pendingMandates;
        this.rejectedMandates = rejectedMandates;
        this.bankName = bankName;
        this.billerName = billerName;
    }
}
