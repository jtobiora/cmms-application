package ng.upperlink.nibss.cmms.dto.nibss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ng.upperlink.nibss.cmms.dto.UserRequest;


@Data
public class NibssRequest extends UserRequest {
    private String staffNumber;

    private String userAuthorisationType;

    @JsonIgnore
    private String loginURL;
}
