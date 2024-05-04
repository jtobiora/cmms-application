package ng.upperlink.nibss.cmms.dto.biller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private BigDecimal amount=BigDecimal.ZERO;

//    @NotNull(message = "please select a biller")
////    private Long billerId;
//
    @JsonIgnore
    private Biller biller;

    private String description;

}
