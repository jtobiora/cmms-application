package ng.upperlink.nibss.cmms.model.mandate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="SharingFormular",schema = Constants.SCHEMA_NAME)
public class SharingFormular implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long billerId;

    private Long beneficiaryId;

    private BigDecimal fee;

}
