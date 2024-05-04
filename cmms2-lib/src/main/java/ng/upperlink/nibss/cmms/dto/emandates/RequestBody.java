package ng.upperlink.nibss.cmms.dto.emandates;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class RequestBody {

    private AuthParam auth;

    List<EmandateRequest> emandateRequests ;
}

