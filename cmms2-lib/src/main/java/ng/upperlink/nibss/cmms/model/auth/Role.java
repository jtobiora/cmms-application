package ng.upperlink.nibss.cmms.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.EnumConstraint;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.view.Detail;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", Constants.CREATED_AT, Constants.UPDATED_AT})
@Auditable
@Table(name = "Role", schema = Constants.SCHEMA_NAME)
public class Role extends SuperModel implements Serializable{

    @Column(name = "Id")
    @JsonView({Detail.BVNClient.class,Detail.ThirdParty.class})
    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonView({Detail.BVNClient.class,Detail.ThirdParty.class})
    @JsonProperty("name")
    @NotNull(message = "Role name cannot be null")
    @Column(name = "Name", unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "RoleType")
    @NotNull(message = "Role Type cannot be null")
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "Description")
    @JsonView({Detail.BVNClient.class,Detail.ThirdParty.class})
    @JsonProperty("description")
    private String description;

    @Column(name = "Activated")
    @JsonProperty("activated")
    private boolean activated = true;

    @Column(name = "UserType")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "RolePrivileges", joinColumns = @JoinColumn(name = "RoleId", referencedColumnName = "Id"), inverseJoinColumns = @JoinColumn(name = "PrivilegeId", referencedColumnName = "Id"))
    private Set<Privilege> privileges = new HashSet<>();

       //added modifications
    @Column(name = "AuthorizationType",nullable = true)
    @NotBlank(message = "user authorization type is required")
    @EnumConstraint(enumClass = MakerCheckerType.class, message = Constants.UNKNOWN_MAKER_CHECKER_TYPE)
    private String userAuthorisationType;

    public Role(Long id, RoleName name, UserType userType) {
        this.id = id;
        this.name = name;
        this.userType = userType;
    }

    public Role(RoleName name, UserType userType){
        this.name = name;
        this.userType = userType;
    }
}
