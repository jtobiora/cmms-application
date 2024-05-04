package ng.upperlink.nibss.cmms.model.mandate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.enums.MandateFrequency;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.Rejection;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.Product;
import ng.upperlink.nibss.cmms.util.JsonDateSerializer;
import ng.upperlink.nibss.cmms.util.JsonDateTimeDeserializer;
import ng.upperlink.nibss.cmms.view.Views;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Auditable
@AllArgsConstructor
@Table(name = "Mandate", schema = Constants.SCHEMA_NAME)
public class Mandate extends SuperModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    private String mandateCode;

    private String subscriberCode;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    // @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biller_id")
    private Biller biller;

    private String email;

    private String payerName;

    private String payerAddress;

    @NotBlank(message = "Account Name cannot be Blank")
    private String accountName;

    @NotBlank(message = "Account name cannot be blank")
    private String accountNumber;

    @JsonView(Views.Public.class)
    private String narration;

    private String mandateImage;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date startDate;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date endDate;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date dateCreated;

    //@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    private int frequency;

    @ManyToOne(fetch = FetchType.EAGER)
    private MandateStatus status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User approvedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User acceptedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User authorizedBy;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date dateApproved;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date dateAccepted;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date dateAuthorized;

    private String phoneNumber;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private Rejection rejection;

    @JsonIgnore
    @Transient
    private String validityDateRange;

    private String workflowStatus;

    @Transient
    @JsonIgnore
    private MultipartFile uploadFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User lastActionBy;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    @JsonIgnore
    private Date dateSuspended;

    private boolean mandateAdviceSent;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date nextDebitDate;

    @JsonIgnore
    private int kycLevel;

    @JsonIgnore
    private String bvn;

    private int requestStatus;

    @JsonIgnore
    private int bankNotified;

    @JsonIgnore
    private int billerNotified;

    @JsonIgnore
    @Column(nullable = true)
    private String lastApiNotificationResponse;

    @JsonIgnore
    @Enumerated(EnumType.ORDINAL)
    private ServiceType serviceType;   //(1)- postpaid  (2) prepaid

    @Enumerated(EnumType.STRING)
    private MandateRequestType mandateType; //(1) -Fixed mandate (2)Variable Mandate

    @Enumerated(EnumType.STRING)
    private MandateCategory mandateCategory; //(1) -Paper mandate (2)E-Mandate

    private boolean fixedAmountMandate;

    private BigDecimal variableAmount;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    private Date dateModified;

    @Transient
    private MandateFrequency mandateFrequency;

    private String scheduleTime;

    @JsonIgnore
    private String mandateOwnerKey;

    public Mandate() {
        this.requestStatus = Constants.STATUS_ACTIVE;
        this.serviceType = ServiceType.POSTPAID;
    }

    @PrePersist
    public void updateDateModified() {
        this.dateModified = new Date();
    }
}
