package ng.upperlink.nibss.cmms.model.biller;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.enums.BillerOwner;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.emandate.EmandateConfig;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;


@Data
@Entity
@Auditable
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Biller", schema = Constants.SCHEMA_NAME)
@ToString(exclude = {"fee"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Biller extends Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "accountName")
    private String accountName;

    @JsonIgnore
    @NotNull(message = "Apikey was not generated, please try again")
    @Column(name = "apiKey")
    private String apiKey;

    @JsonIgnore
    @Column(name = "autoApproveOnApi")
    private String autoApproveOnAPI;

    @JsonIgnore
    @Column(name = "billingMandateCode")
    private String billingMandateCode;

    @Column(name = "bvn")
    @JsonView(Views.Public.class)
    private String bvn;

    @JsonIgnore
    private Integer kycLevel;

    @JsonIgnore
    @Column(name = "mandateStatusNotificationUrl")
    private String mandateStatusNotificationUrl;

    @JsonIgnore
    @Column(name = "paymentStatustificationUrl")
    private String paymentStatusNotificationUrl;

    @JsonIgnore
    @Column(name = "slaAttachmentPath")
    private String slaAttachmentPath;

    @JsonIgnore
    @Lob
    @Column(name = "sla_attachment")
    private byte[] slaAttachment;

    @JsonIgnore
    @Column(name = "sla_attachment_content_type")
    private String slaAttachmentContentType;

    @JsonIgnore
    @OneToMany(mappedBy = "biller", fetch = FetchType.LAZY)
    private Set<Fee> fee;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JoinColumn(  referencedColumnName ="Id" )
    private Bank bankAsBiller ;

//    @JsonIgnore
    @OneToOne
    @JsonView(Views.Public.class)
    @JoinColumn(name = "EmandateConfig", referencedColumnName = "Id")
    private EmandateConfig emandateConfig;

    @Column
    @JsonView(Views.Public.class)
    private String billerOwner;

//    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Public.class)
    private Bank bank;

    public Biller(Long id,String name, String rcNumber, String apiKey, String billerOwner) {
        super(id,name, rcNumber);
        this.apiKey = apiKey;
        this.billerOwner = billerOwner;
    }
    public Biller(Long id,String name, String rcNumber) {
        super(id,name, rcNumber);
        this.apiKey = apiKey;
        this.billerOwner = billerOwner;
    }

    public Biller(Long id,String name, String rcNumber, String apiKey, String billerOwner,BillerOwner ownerType) {
        super(id,name, rcNumber,ownerType);
        this.apiKey = apiKey;
        this.billerOwner = billerOwner;
    }

    public Biller(Long id, EntityType entityType, InitiatorActions actionInitiated,
                  AuthorizationStatus authorizationStatus,String jsonData, String reason, boolean activated, String name,
                  String description, String rcNumber, Industry industry, String accountNumber) {
        super(id, entityType, actionInitiated, authorizationStatus, jsonData, reason, activated, name, description,
                rcNumber, industry);
        this.accountNumber = accountNumber;
    }

    public Biller(Long id, String name, String rcNumber,String accountNumber,String description,String accountName) {
        super(id, name, rcNumber,description);
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }

    public Biller (Long id, String unapprovedJson){
        Biller biller = unapprovedJson ==null ? new Biller() : new Gson().fromJson(unapprovedJson, Biller.class);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Biller biller = (Biller) o;
        if (biller.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), biller.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}