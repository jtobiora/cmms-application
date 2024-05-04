package ng.upperlink.nibss.cmms.model.auth;

import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.Module;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.SuperModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Privilege", schema = Constants.SCHEMA_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {"name","module"}))
public class Privilege extends SuperModel implements Serializable {

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name")
    @NotNull
    private String name;

    @Column(name = "Url")
    @NotNull
    private String url;

    @Column(name = "Module")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Module module;

    @Column(name = "Description")
    private String description;

    @Column(name = "Activated")
    private boolean activated = true;
}
