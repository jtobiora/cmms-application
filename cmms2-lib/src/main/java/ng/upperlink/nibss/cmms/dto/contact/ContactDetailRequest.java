package ng.upperlink.nibss.cmms.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetailRequest {

    @NotBlank(message = "street number is required")
    private String streetNumber;

    @NotNull(message = "city is required")
    private String city;

    @NotNull(message = "local government area is required")
    private Long lgaId;
}
