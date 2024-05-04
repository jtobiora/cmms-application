package ng.upperlink.nibss.cmms.dto.auth;

import lombok.Data;
import ng.upperlink.nibss.cmms.dto.Id;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class NewPrivilegeRequest extends Id {

    @NotNull
    private List<Long> privilegeIds;

}
