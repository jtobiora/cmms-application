package ng.upperlink.nibss.cmms.model;

import ng.upperlink.nibss.cmms.enums.Constants;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "WebAppAuditEntry",schema = Constants.SCHEMA_NAME)
public class WebAppAuditEntry  implements Serializable{

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @Column(name = "UserName")
    private String user;

    @Column(name = "Action")
    @Enumerated(EnumType.ORDINAL)
    private WebAuditAction action;

    @Column(name = "DateCreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "ClassName")
    private String className;

    @Column(name = "EntityId")
    private String entityId;

    @Column(name = "OldObject")
    @Lob
    private String oldObject;

    @Column(name = "NewObject")
    @Lob
    private String newObject;

    @PrePersist
    protected void onSave() {
        dateCreated = new Date();
    }

}
