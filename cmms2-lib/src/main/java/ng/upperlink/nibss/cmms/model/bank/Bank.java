package ng.upperlink.nibss.cmms.model.bank;

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
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Auditable
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Bank",schema = Constants.SCHEMA_NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bank  extends AuthorizationTable implements Serializable  {

    @NotNull(message = "Bank name cannot be null")
    @Column(name = "Name")
    private String name;

    @NotNull (message = "Bank code cannot be null")
    @Column(name = "Code")
    private String code;

    @JsonView(Views.Public.class)
    private String nipBankCode;

    @JsonIgnore
    @NotNull(message = "Apikey was not generated, please try again")
    @Column
    private String apiKey;

//    @JsonIgnore
    @OneToOne
    @JsonView(Views.Public.class)
    @JoinColumn(name = "EmandateConfig", referencedColumnName = "Id")
    private EmandateConfig emandateConfig;

    public Bank(Long id,String code, String name){
        super(id);
        this.code =code;
        this.name =name;
    }

    public Bank(Long id, EntityType entityType, InitiatorActions actionInitiated, AuthorizationStatus authorizationStatus,
                String jsonData, String reason, boolean activated, String name, String code,
                String nipBankCode, String apiKey,User createdBy, User updatedBy,User approvedBy) {
        super(id, entityType, actionInitiated, authorizationStatus, jsonData, reason, activated,createdBy,updatedBy,approvedBy);
        this.name = name;
        this.code = code;
        this.nipBankCode = nipBankCode;
        this.apiKey = apiKey;
//        this.notificationUrl = notificationUrl;
    }
    public Bank(Long id, String code, String name, String apiKey) {
        super(id);
        this.name = name;
        this.code = code;
        this.apiKey = apiKey;
    }
    public Bank(Long id, String code, String name,String nipBankCode, String apiKey) {
        super(id);
        this.name = name;
        this.code = code;
        this.nipBankCode = nipBankCode;
        this.apiKey = apiKey;
    }
}
