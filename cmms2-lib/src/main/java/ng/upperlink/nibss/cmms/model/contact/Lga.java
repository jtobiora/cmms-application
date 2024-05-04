package ng.upperlink.nibss.cmms.model.contact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.view.Detail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", Constants.CREATED_AT, Constants.UPDATED_AT })
@Table(name="Lga",schema = Constants.SCHEMA_NAME)
public class Lga extends SuperModel implements Serializable{

    @JsonView(Detail.BVNClient.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    @NotNull
    @Column(unique = true,name = "Name")
    private String name;

    @JsonIgnore
    @Column(name = "Code")
    private String code;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StateId", referencedColumnName = "Id")
    private State state;

    public Lga(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Lga(Long id){
        this.id = id;
    }
}
