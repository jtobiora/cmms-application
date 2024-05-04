package ng.upperlink.nibss.cmms.model.emandate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;

import javax.persistence.*;
import java.io.Serializable;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString
@Table(name="EmandatePasswordRecoveryCode",schema = Constants.SCHEMA_NAME)
public class EmandatePasswordRecoveryCode extends SuperModel implements Serializable {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    @Basic(optional = false)
    @JoinColumn(name="ObjectId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private AuthorizationTable object;

    @Basic(optional=false)
    @Column(name ="RecoveryCode")
    private String recoveryCode;

    @Basic(optional=false)
    @Column(name ="Status")
    private boolean status = false;
}

