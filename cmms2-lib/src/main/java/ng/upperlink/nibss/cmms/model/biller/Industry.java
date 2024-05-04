package ng.upperlink.nibss.cmms.model.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Industry.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Auditable
@Entity
@Table(name = "Industry", schema = Constants.SCHEMA_NAME)
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Industry extends AuthorizationTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
//    private User createdBy;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
//    private User updatedBy;

//    public Industry(Long id, String name, String description) {
//        super(id);
//        this.name = name;
//        this.description = description;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Industry industry = (Industry) o;
        if (industry.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), industry.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
