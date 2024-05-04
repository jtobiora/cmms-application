package ng.upperlink.nibss.cmms.dto.biller;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class IndustryRequest{

    private Long id;
//
    @NotNull(message = "{industry.name.notNull}")
    private String name;

//    @NotNull(message = "{industry.description.notNull}")
    private String description;
}
