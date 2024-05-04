package ng.upperlink.nibss.cmms.dto.contact;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class StateRequest {

    private Long id;

    @NotBlank(message = "state name is required")
    private String name;

    @NotNull(message = "country Id is required")
    private Long countryId;
}