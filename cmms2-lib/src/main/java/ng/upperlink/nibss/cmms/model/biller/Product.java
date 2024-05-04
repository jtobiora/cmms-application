package ng.upperlink.nibss.cmms.model.biller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.authorization.AuthorizationTable;
import ng.upperlink.nibss.cmms.view.Views;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Auditable
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products",schema = Constants.SCHEMA_NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product extends AuthorizationTable implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    private BigDecimal amount=BigDecimal.ZERO;

    // @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    private Biller biller;

    @JsonView(Views.Public.class)
    private String description;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private User createdBy;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private User updateddBy;

    public Product(Long id, String name, BigDecimal amount, String description){
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
    }

}
