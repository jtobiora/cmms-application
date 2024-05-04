package ng.upperlink.nibss.cmms.dto.biller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ng.upperlink.nibss.cmms.dto.UserRequest;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class BillerUserRequest extends UserRequest {
//    @NotBlank(message = "staff number is required")
    private String staffNumber;

    @JsonIgnore
    private Biller biller;

    @NotEmpty(message = "Biller is required")
    private Long billerId;

//    @JsonIgnore
//    private String loginUrl;

}
