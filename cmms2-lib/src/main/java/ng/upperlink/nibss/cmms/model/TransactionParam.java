package ng.upperlink.nibss.cmms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="TransactionParam",schema = Constants.SCHEMA_NAME)
public class TransactionParam implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sessionId")
    private String sessionId;

    @Column(name = "creditResponseCode")
    private String creditResponseCode;

    @Column(name = "debitResponseCode")
    private String debitResponseCode;

    @Column(name = "dateCreated")
    private Timestamp dateCreated;
    
    @Column(name = "amountDebited")
    private BigDecimal amountDebited;
    
    @Column(name = "amountCredited")
    private BigDecimal amountCredited;

    //@Column(name = "transaction")
//    @ManyToOne(fetch = FetchType.LAZY,nullable=false)
//    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(referencedColumnName ="id")
    private Transaction transaction;

}
