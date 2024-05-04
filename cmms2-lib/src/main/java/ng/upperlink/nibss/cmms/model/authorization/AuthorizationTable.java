
package ng.upperlink.nibss.cmms.model.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Auditable
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "AuthorizationTable",schema = Constants.SCHEMA_NAME)
public class AuthorizationTable extends SuperModel implements Serializable{

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private InitiatorActions actionInitiated;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private AuthorizationStatus  authorizationStatus;

    @JsonIgnore
    @Lob
    @Column(name = "jsonData")
    private String jsonData;

    @JsonView(Views.Public.class)
    private String reason;

    @Column(name = "Activated")
    @JsonView(Views.Public.class)
    private boolean activated;

    @ManyToOne
    @JsonIgnoreProperties("")
    @JsonView(Views.Public.class)
    private User createdBy;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JsonIgnoreProperties("")
    private User updatedBy;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JsonIgnoreProperties("")
    private User approvedBy;

    public AuthorizationTable (Long id)
    {
        this.id =id;
    }

    public AuthorizationTable(Long id ,EntityType entityType, InitiatorActions actionInitiated, AuthorizationStatus authorizationStatus, String jsonData, String reason, boolean activated) {
        this.id = id;
        this.entityType = entityType;
        this.actionInitiated = actionInitiated;
        this.authorizationStatus = authorizationStatus;
        this.jsonData = jsonData;
        this.reason = reason;
        this.activated = activated;
    }
}