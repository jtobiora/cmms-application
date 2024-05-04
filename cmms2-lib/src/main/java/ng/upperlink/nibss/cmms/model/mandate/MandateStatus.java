package ng.upperlink.nibss.cmms.model.mandate;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.MandateStatusType;
import ng.upperlink.nibss.cmms.view.Views;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="MandateStatus",schema = Constants.SCHEMA_NAME)
public class MandateStatus implements Serializable {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private String mailDescription;

    @JsonView(Views.Public.class)
    private String code;

    @Column
    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private MandateStatusType statusName;
}
