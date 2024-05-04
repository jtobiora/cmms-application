package ng.upperlink.nibss.cmms.model.biller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.BillerOwner;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationStatus;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.enums.makerchecker.InitiatorActions;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Company.
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Auditable
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Company", schema = Constants.SCHEMA_NAME)
public class Company extends AuthorizationTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    @NotNull(message = "Company name is required")
//    @Size(min = 6, message = "Company name must not be less than {min}")
    private String name;

    @Column(name = "description")
    @JsonView(Views.Public.class)
    @NotNull(message = "Company description is required")
//    @Size(min = 5, message = "Company Description must not be less than {min}")
    private String description;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private BillerOwner ownerType;

    @Column(name = "rcNumber")
    @JsonView(Views.Public.class)
    @NotNull(message = "Rc number required")
//	@Size(min = 5,max =5, message = "Rc number must be {max}")
    private String rcNumber;

    @ManyToOne
    @JsonView(Views.Public.class)
    @JsonIgnoreProperties("companies")
    private Industry industry;

    public Company(Long id, String name, String rcNumber,BillerOwner ownerType) {
        super(id);
        this.name = name;
        this.rcNumber = rcNumber;
        this.ownerType =ownerType;
    }
    public Company(Long id,String name,String rcNumber,String description) {
        super(id);
        this.name = name;
        this.rcNumber = rcNumber;
        this.description = description;
    }
    public Company(Long id, String name, String rcNumber) {
        super(id);
        this.name = name;
        this.rcNumber = rcNumber;
    }
    public Company( String name, String rcNumber) {

        this.name = name;
        this.rcNumber = rcNumber;
    }

    public Company(Long id, EntityType entityType,InitiatorActions actionInitiated,
                   AuthorizationStatus authorizationStatus,String jsonData, String reason, boolean activated, String name,
                   String description, String rcNumber, Industry industry) {
        super(id, entityType, actionInitiated, authorizationStatus, jsonData, reason, activated);
        this.name = name;
        this.description = description;
        this.rcNumber = rcNumber;
        this.industry = industry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
        if (company.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), company.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
