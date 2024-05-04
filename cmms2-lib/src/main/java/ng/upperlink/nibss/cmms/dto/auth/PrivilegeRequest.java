package ng.upperlink.nibss.cmms.dto.auth;

import ng.upperlink.nibss.cmms.enums.Module;
import ng.upperlink.nibss.cmms.errorHandler.EnumConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeRequest {

    private Long id;

    @NotBlank(message = "task name is required")
    private String name;

    @NotBlank(message = "task url is required")
    private String url;

    @NotNull(message = "module is required")
    @EnumConstraint(enumClass = Module.class, message = "Invalid module.")
    private String module;

    private String description;

}
