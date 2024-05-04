package ng.upperlink.nibss.cmms.dto.auth;

import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.RoleType;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.model.auth.Privilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse implements Serializable {

    private Long id;

    private RoleName name;

    private RoleType roleType;

    private String description;

    private UserType userType;

    private boolean activated = true;

    private Set<Privilege> tasks = new HashSet<>();
}
