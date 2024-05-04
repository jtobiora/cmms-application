package ng.upperlink.nibss.cmms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.dto.auth.LoginRequest;
import org.hibernate.validator.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest extends LoginRequest {

    @NotBlank(message = "new password is required")
    private String newPassword;

}
