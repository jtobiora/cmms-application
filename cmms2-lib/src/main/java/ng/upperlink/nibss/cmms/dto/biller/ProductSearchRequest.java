package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchRequest {
    private String description;
    private String productName;
    private String billerName;
    private String activated;
}
