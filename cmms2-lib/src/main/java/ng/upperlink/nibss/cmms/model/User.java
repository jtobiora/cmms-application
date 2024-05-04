package ng.upperlink.nibss.cmms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ng.upperlink.nibss.cmms.embeddables.ContactDetails;
import ng.upperlink.nibss.cmms.embeddables.Name;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.model.auth.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Auditable
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Users",schema = Constants.SCHEMA_NAME)
@ToString(exclude = {"contactDetails","roles"})
public class User extends SuperModel implements Serializable {

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @Embedded
    private Name name;

    @NotNull
    @Column(name = "PhoneNumber", unique = true)
    private String phoneNumber;

    @NotNull
    @Column(name = "EmailAddress", unique = true)
    private String emailAddress;

    @Column(name = "Password")
    @JsonIgnore
    @NotNull
    private String password;

    @Embedded
    private ContactDetails contactDetails = new ContactDetails();

    @Column(name = "ChangePassword")
    private boolean change_password = false;

    @Column(name = "Activated")
    private boolean activated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "UserRoles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "UserType")
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonIgnore
    @Column(name = "PasswordUpdatedAt")
    @Temporal(TemporalType.DATE)
    private Date passwordUpdatedAt;

    @JsonIgnore
    @Lob
    @Column(name = "jsonData")
    private String jsonData;

    @Enumerated(EnumType.STRING)
    private AuthorizationStatus authorizationStatus;

    private String reason;

    @JsonIgnore
    @Column(name = "loggedToken")
    private String loggedToken;

    public User(Long id, String emailAddress, String password, boolean changePassword, Date passwordUpdatedAt) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.change_password = changePassword;
    }
    public User(Long id, Name name, String emailAddress,UserType userType) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.userType = userType;
    }
    public User(Long id, Name name, String emailAddress, boolean activated, UserType userType) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.activated = activated;
        this.userType = userType;
    }

    public User(Long id, Name name, String emailAddress) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public User(Name name, String emailAddress,Date createdAt,Set<Role> roles) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null) return false;
        return emailAddress != null ? emailAddress.equals(user.emailAddress) : user.emailAddress == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        return result;
    }
}
