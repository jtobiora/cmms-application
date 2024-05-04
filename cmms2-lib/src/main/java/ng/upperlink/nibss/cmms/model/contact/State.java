package ng.upperlink.nibss.cmms.model.contact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.Auditable;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.view.Detail;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", Constants.CREATED_AT, Constants.UPDATED_AT })
@Auditable
@Table(name = "State", schema = Constants.SCHEMA_NAME)
public class State extends SuperModel implements Serializable {

    @Column(name = "Id")
    @JsonView(Detail.BVNClient.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "Name", unique = true)
    private String name;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CountryId", referencedColumnName = "Id")
    private Country country;

    public State(Long id, String name){
        this.id = id;
        this.name = name;
    }

}
