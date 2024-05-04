package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthParam {

    @NotBlank(message = "Username can not be blank")
    String username;

    @NotBlank(message = "Password can not be blank")
    String password;

    @NotBlank(message = "API key can not be blank")
    String apiKey;
}
