package ng.upperlink.nibss.cmms.embeddables.makerchecker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.util.JsonDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MakerChecker {

    @JsonIgnore
    @Lob
    @Column(name = "UnapprovedData")
    private String unapprovedData;

    @Column(name = "ApprovalStatus")
    private Boolean approvalStatus;

    @Column(name = "ApprovalReason")
    private String approvalReason;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "ApprovalDate")
    private Date approvalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovedBy", referencedColumnName = "Id")
    private User approvedBy;
}