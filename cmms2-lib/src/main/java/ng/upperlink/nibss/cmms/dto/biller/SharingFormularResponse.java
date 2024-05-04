package ng.upperlink.nibss.cmms.dto.biller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.mandate.Beneficiary;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingFormularResponse {
    private Long id;

    private Biller biller;

    private Beneficiary beneficiary;

    private BigDecimal fee;
}
