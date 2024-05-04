package ng.upperlink.nibss.cmms.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.EmailPrivilege;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.makerchecker.EntityType;
import ng.upperlink.nibss.cmms.model.SuperModel;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class EmailConfiguration extends SuperModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String email;

    @Column
    private EmailPrivilege emailPrivilege;

    @Column
    private String ownerApiKey;

    @Column
    private EntityType entityType;

    @Column
    private RoleName roleName;
}
