package ng.upperlink.nibss.cmms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.auth.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "NibssUser", schema = Constants.SCHEMA_NAME)
@ToString(exclude = {"createdBy", "updatedBy"})
public class NibssUser extends User implements Serializable {

    @JsonIgnore
    @Column(name = "StaffNumber")
    private String staffNumber;

    //    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private User createdBy;

    //    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private User updatedBy;


    public NibssUser(Long id, String unapprovedJson) {
        Long userId = getId();
        userId = id;
        NibssUser nibss = unapprovedJson == null ? new NibssUser() : new Gson().fromJson(unapprovedJson, NibssUser.class);
        this.staffNumber = nibss.getStaffNumber();
        this.createdBy = nibss.getCreatedBy();
        setUpdatedBy(nibss.getUpdatedBy());
    }

    public NibssUser(Name name, String email,Date createdAt,Set<Role> roles){
        super(name,email,createdAt,roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NibssUser nibssUser = (NibssUser) o;

        return staffNumber != null ? staffNumber.equals(nibssUser.staffNumber) : nibssUser.staffNumber == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (staffNumber != null ? staffNumber.hashCode() : 0);
        return result;
    }
}
