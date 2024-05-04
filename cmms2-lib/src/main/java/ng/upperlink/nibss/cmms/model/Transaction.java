package ng.upperlink.nibss.cmms.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Auditable
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Transactions",schema = Constants.SCHEMA_NAME)
public class Transaction implements Serializable{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    
    @Column(name = "bearer")
    @Enumerated(EnumType.ORDINAL)
    private FeeBearer bearer;
    
    @Column(name = "splitType")
    @Enumerated(EnumType.ORDINAL)
    private SplitType splitType;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(referencedColumnName ="id")
    private Bank bank;

    @Column(name="accountNumber", nullable = true)
    private String accountNumber;

    @Column(name="accountName", nullable = true)
    private String accountName;
    
    @Column(name="billerDebitAccountNumber", nullable = true)
    private String billerDebitAccountNumber;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Bank billerDebitBank;

    @Column(name="billerDebitAccountName", nullable = true)
    private String billerDebitAccountName;
    
    @Column(name = "defaultFee")
    private BigDecimal defaultFee;
    
    @Column(name = "fee")
    private BigDecimal fee;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @OneToMany(mappedBy = "transaction")
    private Set<TransactionParam> params;
    
    @Column(name = "billed")
    private boolean billed;
    
    @Column(name = "markedUp", nullable = true)
    private boolean markedUp;
    
    @Column(name = "billableAtTransactionTime")
    private boolean billableAtTransactionTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName ="id")
    private Mandate mandate;

    @Enumerated(EnumType.STRING)
    @Column(name="transactionType")
    private Channel transactionType;
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "transaction")
//    private Set<TransactionParam> transactionParam = new HashSet<>();

    @Column(name = "numberOfCreditTrials")
    private int numberOfCreditTrials;

    @Column(name = "numberOfTrials")
    private int numberOfTrials;

    @Column(name = "dateCreated")
    private Date dateCreated;
    
    @Column(name = "paymentDate")
    private Date paymentDate;
    
    @Column(name = "dateModified")
    private Timestamp dateModified;

    @Column(name = "lastDebitDate")
    private Date lastDebitDate;

    @Column(name = "lastCreditDate")
    private Date lastCreditDate;
    
    @Column(name="successfulSessionId", nullable = true)
    private String successfulSessionId;
    
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private TransactionStatus status;

    @PrePersist
    public void updateDateModified() {
        this.dateModified = new Timestamp(System.currentTimeMillis());
    }
}

