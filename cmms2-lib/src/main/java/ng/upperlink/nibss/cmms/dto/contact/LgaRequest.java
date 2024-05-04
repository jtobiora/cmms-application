package ng.upperlink.nibss.cmms.dto.contact;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
public class LgaRequest {

    private Long id;

    @NotBlank(message = "lga name is required")
    private String name;

    @NotNull(message = "state is required.")
    private Long stateId;
}
