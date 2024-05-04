package ng.upperlink.nibss.cmms.model.mandate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.bank.Bank;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Auditable
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Beneficiary",schema = Constants.SCHEMA_NAME)
public class Beneficiary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name",nullable = false)
    private String beneficiaryName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Bank bank;

    @Column(name="accountNumber",nullable = false)
    private String accountNumber;

    @Column(name="accountName",nullable = false)
    private String accountName;

    @Column(name="activated",nullable = true)
    private boolean activated;

}
