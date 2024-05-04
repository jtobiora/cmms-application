package ng.upperlink.nibss.cmms.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Id {

    @NotNull(message = "Object is required")
    private Long id;

}
