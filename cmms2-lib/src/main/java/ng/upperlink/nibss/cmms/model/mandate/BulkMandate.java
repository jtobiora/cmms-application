package ng.upperlink.nibss.cmms.model.mandate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Constants;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="BulkMandate",schema = Constants.SCHEMA_NAME)
public class BulkMandate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Long id;

    @Lob
    @JsonIgnore
    private String mandateInJson;

    @Column(name="mandateId",unique = true)
    private Long mandateId;

    private String sessionId;
}
