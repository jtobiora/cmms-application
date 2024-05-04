package ng.upperlink.nibss.cmms.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

//    @NotBlank(message = "emailAddress address is required")
    private String emailAddress;

    @NotBlank(message = "password is required")
    private String password;

    private String token;

}
