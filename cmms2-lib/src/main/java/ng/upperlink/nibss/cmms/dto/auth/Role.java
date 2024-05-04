package ng.upperlink.nibss.cmms.dto.auth;

import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.errorHandler.EnumConstraint;
import ng.upperlink.nibss.cmms.errorHandler.NotNumberConstraint;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class Role {

    private Long id;

    @NotNull(message = "Role name is required")
    private RoleName name;

    @NotNull(message = "Role name is required")
    private RoleType roleType;

    @NotNull(message = "user type is required.")
    private UserType userType;

    @NotNull(message = "Role authorization type is required")
    @EnumConstraint(enumClass = MakerCheckerType.class, message = Constants.UNKNOWN_MAKER_CHECKER_TYPE)
    private String userAuthorisationType;

    private String description;

}
