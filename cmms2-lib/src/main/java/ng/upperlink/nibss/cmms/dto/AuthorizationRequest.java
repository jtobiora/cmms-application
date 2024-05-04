package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ng.upperlink.nibss.cmms.enums.makerchecker.AuthorizationAction;

import javax.validation.constraints.NotNull;

@Data
public class AuthorizationRequest extends Reason{
    @JsonIgnore
    AuthorizationAction authorization;
}
