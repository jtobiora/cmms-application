package ng.upperlink.nibss.cmms.model.pssp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.User;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="PsspUser",schema = Constants.SCHEMA_NAME)
public class PsspUser extends User implements Serializable {

    @JsonIgnore
    @Column(name = "StaffNumber")
    private String staffNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pssp", referencedColumnName = "Id")
    private Pssp pssp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private User createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private User updatedBy;

}
