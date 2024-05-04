package ng.upperlink.nibss.cmms.model.pssp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.model.bank.Bank;
import ng.upperlink.nibss.cmms.model.biller.Industry;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Auditable
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Pssp",schema = Constants.SCHEMA_NAME)
public class Pssp extends AuthorizationTable implements Serializable {

    @NotBlank
    @Column(name = "Name")
    private String psspName;

    @NotBlank
    @Column(name = "Code",unique = true)
    private String psspCode;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "accountName")
    private String accountName;

    @JsonIgnore
    @NotNull(message = "Apikey was not generated, please try again")
    @Column(name = "apiKey",unique = true)
    private String apiKey;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bankCode", referencedColumnName = "Code")
//    private Bank bank;

//    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;


    @Column(name = "bvn")
    private String bvn;

    @Column
    private String psspOwner;
//
//    @ManyToOne
//    @JsonIgnoreProperties("")
//    @JsonIgnore
//    private User createdBy;
//
//    @ManyToOne
//    @JsonIgnoreProperties("")
//    @JsonIgnore
//    private User updatedBy;

    @Column(name = "Activated")
    private boolean activated;

    private String description;

    @ManyToOne
    //@JsonIgnoreProperties("companies")
    private Industry industry;

    @Column(name = "rcNumber",unique = true)
    private String rcNumber;

    public Pssp(Long id, String psspName, String psspCode, String apiKey, String rcNumber) {
        super(id);
        this.psspName = psspName;
        this.psspCode = psspCode;
        this.apiKey = apiKey;
        this.rcNumber = rcNumber;
    }
}
