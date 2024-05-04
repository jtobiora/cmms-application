package ng.upperlink.nibss.cmms.model.biller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
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
@Table(name = "BillerUser", schema = Constants.SCHEMA_NAME)
public class BillerUser extends User implements Serializable {

    @JsonIgnore
    @Column(name = "StaffNumber")
    private String staffNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private User createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private User updatedBy;

    //    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biller", referencedColumnName = "Id")
    private Biller biller;

    public BillerUser(Long id, String unapprovedJson) {
        BillerUser billerUser = unapprovedJson == null ? new BillerUser() : new Gson().fromJson(unapprovedJson, BillerUser.class);
        this.setId(billerUser.getId());
        this.setName(billerUser.getName());
        this.setPhoneNumber(billerUser.getPhoneNumber());
        this.setEmailAddress(billerUser.getEmailAddress());
        this.setContactDetails(billerUser.getContactDetails());
        this.setRoles(billerUser.getRoles());
        this.setUserType(billerUser.getUserType());
        this.staffNumber = billerUser.getStaffNumber();
        this.biller = billerUser.biller;
        this.createdBy = billerUser.getCreatedBy();
        this.createdBy = billerUser.createdBy;
        this.updatedBy = billerUser.updatedBy;
        this.createdAt = billerUser.createdAt;
        this.updatedAt = billerUser.updatedAt;

    }
}