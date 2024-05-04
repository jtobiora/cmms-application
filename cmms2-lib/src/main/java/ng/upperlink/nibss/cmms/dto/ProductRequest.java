package ng.upperlink.nibss.cmms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long userId;

    @NotBlank(message = "productId name is required")
    private String name;

    private BigDecimal amount = BigDecimal.ZERO;

    private String description;

    private int status;

}