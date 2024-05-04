package ng.upperlink.nibss.cmms.model;

import ng.upperlink.nibss.cmms.enums.Constants;
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
@Auditable
@Table(name = "Settings", schema = Constants.SCHEMA_NAME)
public class Settings extends SuperModel implements Serializable{

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name")
    @NotNull
    private String name;

    @Column(name = "Value")
    @NotNull
    private String value;

    public Settings(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
