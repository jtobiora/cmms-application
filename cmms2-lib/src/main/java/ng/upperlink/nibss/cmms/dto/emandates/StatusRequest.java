package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class StatusRequest {

    private AuthParam auth;

    String mandateCode;
}
