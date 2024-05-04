package ng.upperlink.nibss.cmms.embeddables;

import ng.upperlink.nibss.cmms.enums.ServicesProvided;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TransactionAmountPerChannel {

    @Enumerated(EnumType.STRING)
    private ServicesProvided servicesProvided;

    @NotNull
    private BigDecimal amount;

}
