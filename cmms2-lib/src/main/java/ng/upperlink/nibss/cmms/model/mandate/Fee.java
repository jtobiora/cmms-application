package ng.upperlink.nibss.cmms.model.mandate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.FeeBearer;
import ng.upperlink.nibss.cmms.enums.SplitType;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Biller;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Auditable
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"biller"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="Fees",schema = Constants.SCHEMA_NAME)
public class Fee implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billerId", referencedColumnName = "Id", nullable = true)
    private Biller biller;

    @Column(name="markUpFee", nullable = true)
    private BigDecimal markUpFee;

    @Column(name="percentageAmount")
    private BigDecimal percentageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="splitType",nullable = false)
    private SplitType splitType;

    @Enumerated(EnumType.STRING)
    @Column(name="feeBearer",nullable = false)
    private FeeBearer feeBearer;

    @Column(name="debitAtTrans", nullable = true)
    private boolean isDebitAtTransactionTime;
    
    @Column(name = "markedUp", nullable = true)
    private boolean markedUp;

    @Column(name="billerDebitAccountNumber", nullable = true)
    private String billerDebitAccountNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(referencedColumnName = "Id", name = "bank")
    private Bank bank;

    @Column(name="accountNumber", nullable = true)
    private String accountNumber;

    @Column(name="accountName", nullable = true)
    private String accountName;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "billerDebitBank", referencedColumnName = "Id")
    private Bank billerDebitBank;

    @Column(name="billerDebitAccountName", nullable = true)
    private String billerDebitAccountName;

    @Column(name="defaultFee", nullable = false)
    private BigDecimal defaultFee;

}
