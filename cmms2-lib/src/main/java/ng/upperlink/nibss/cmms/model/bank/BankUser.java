package ng.upperlink.nibss.cmms.model.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.MakerCheckerType;
import ng.upperlink.nibss.cmms.model.User;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="BankUser",schema = Constants.SCHEMA_NAME)
public class BankUser extends User implements Serializable {

    @JsonIgnore
    @Column(name = "StaffNumber")
    private String staffNumber;


    @Enumerated(EnumType.STRING)
    @Column(name = "MakerCheckerType")
    private MakerCheckerType makerCheckerType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private User createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private User updatedBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userBank", referencedColumnName = "Id")
    private Bank userBank;

    public BankUser(Long id, String unapprovedJson) {
        BankUser bankUser = unapprovedJson == null ? new BankUser() : new Gson().fromJson(unapprovedJson, BankUser.class);
        this.setId(bankUser.getId());
        this.setName(bankUser.getName());
        this.setPhoneNumber(bankUser.getPhoneNumber());
        this.setEmailAddress(bankUser.getEmailAddress());
        this.setContactDetails(bankUser.getContactDetails());
        this.setRoles(bankUser.getRoles());
        this.setUserType(bankUser.getUserType());
        this.staffNumber = bankUser.getStaffNumber();
        this.makerCheckerType = bankUser.getMakerCheckerType();
        this.createdBy = bankUser.getCreatedBy();
        this.userBank = bankUser.getUserBank();
        this.createdBy =bankUser.createdBy;
        this.updatedBy =bankUser.updatedBy;
        this.createdAt =bankUser.createdAt;
        this.updatedAt =bankUser.updatedAt;
    }
}
