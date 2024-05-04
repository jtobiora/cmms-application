package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingPattern {

    private Long beneficiaryId;

    private BigDecimal fee;
}
