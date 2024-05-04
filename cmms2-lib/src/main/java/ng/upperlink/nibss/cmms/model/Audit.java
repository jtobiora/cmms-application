package ng.upperlink.nibss.cmms.model;

import ng.upperlink.nibss.cmms.enums.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name="Audit",schema = Constants.SCHEMA_NAME)
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    @Column(name = "MacAddress")
    private String macAddress;
    @Column(name = "IpAddress")
    private String ipAddress;
    @Column(name = "UserName")
    private String userName;
    @Column(name = "AuditCreatedAt")
    private Date auditCreatedAt;
    @Column(name = "Action")
    private String action;
    @Column(name = "AgentCode")
    private String agentCode;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "SyncedAt")
    private Date syncedAt;

    public Audit(String macAddress, String ipAddress, String userName, String action, Date auditCreatedAt, String agentCode) {
        this(macAddress, ipAddress, userName, action, auditCreatedAt);
        this.agentCode = agentCode;
    }

    public Audit(String macAddress, String ipAddress, String userName, String action, Date auditCreatedAt) {
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.action = action;
        this.auditCreatedAt = auditCreatedAt;
    }
}
