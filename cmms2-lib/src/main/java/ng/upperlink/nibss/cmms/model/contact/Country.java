package ng.upperlink.nibss.cmms.model.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.model.SuperModel;
import ng.upperlink.nibss.cmms.view.Detail;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", Constants.CREATED_AT, Constants.UPDATED_AT })
@Table(name="Country",schema = Constants.SCHEMA_NAME)
public class Country extends SuperModel {

    @JsonView(Detail.BVNClient.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    @JsonView(Detail.ThirdParty.class)
    @NotNull
    @Column(unique = true,name = "Name")
    private String name;

}
