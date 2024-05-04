package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;


@Data
@NoArgsConstructor
public class TransactionReportByStateAndLga {

    private String stateName;
    private String lgaName;
    private BigInteger totalVolume = BigInteger.ZERO;
    private BigDecimal totalValue = BigDecimal.ZERO;

    public TransactionReportByStateAndLga(BigInteger accountOpeningVolume, BigDecimal accountOpeningValue, BigInteger additionalService1Volume,
                                          BigDecimal additionalService1Value, BigInteger additionalService2Volume, BigDecimal additionalService2Value,
                                          BigInteger airtimeRechargeVolume, BigDecimal airtimeRechargeValue, BigInteger billPaymentVolume,
                                          BigDecimal billPaymentValue, BigInteger bvnEnrollmentVolume, BigDecimal bvnEnrollmentValue,
                                          BigInteger cashInVolume, BigDecimal cashInValue, BigInteger cashOutVolume, BigDecimal cashOutValue,
                                          BigInteger fundTransferVolume, BigDecimal fundTransferValue, BigInteger othersVolume, BigDecimal othersValue,
                                          String stateName, String lgaName) {
        this.stateName = stateName;
        this.lgaName = lgaName;
        this.totalVolume = totalVolume.add(accountOpeningVolume == null ? BigInteger.ZERO : accountOpeningVolume)
                .add(additionalService1Volume == null ? BigInteger.ZERO : additionalService1Volume)
                .add(additionalService2Volume == null ? BigInteger.ZERO : additionalService2Volume)
                .add(airtimeRechargeVolume == null ? BigInteger.ZERO : airtimeRechargeVolume)
                .add(billPaymentVolume == null ? BigInteger.ZERO : billPaymentVolume)
                .add(bvnEnrollmentVolume == null ? BigInteger.ZERO : bvnEnrollmentVolume)
                .add(cashInVolume == null ? BigInteger.ZERO : cashInVolume)
                .add(cashOutVolume == null ? BigInteger.ZERO : cashOutVolume)
                .add(fundTransferVolume == null ? BigInteger.ZERO : fundTransferVolume)
                .add(othersVolume == null ? BigInteger.ZERO : othersVolume);

        this.totalValue = totalValue.add(accountOpeningValue == null ? BigDecimal.ZERO : accountOpeningValue)
                .add(additionalService1Value == null ? BigDecimal.ZERO : additionalService1Value)
                .add(additionalService2Value == null ? BigDecimal.ZERO : additionalService2Value)
                .add(airtimeRechargeValue == null ? BigDecimal.ZERO : airtimeRechargeValue)
                .add(billPaymentValue == null ? BigDecimal.ZERO : billPaymentValue)
                .add(bvnEnrollmentValue == null ? BigDecimal.ZERO : bvnEnrollmentValue)
                .add(cashInValue == null ? BigDecimal.ZERO : cashInValue)
                .add(cashOutValue == null ? BigDecimal.ZERO : cashOutValue)
                .add(fundTransferValue == null ? BigDecimal.ZERO : fundTransferValue)
                .add(othersValue == null ? BigDecimal.ZERO : othersValue);
    }
}
