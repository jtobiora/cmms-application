package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;


@Data
@NoArgsConstructor
public class Dashboard {

    //total
    private BigInteger totalVolume = BigInteger.ZERO;
    private BigDecimal totalValue = BigDecimal.ZERO;

    //total per transaction type
    private BigInteger accountOpeningVolume;
    private BigDecimal accountOpeningValue;

    private BigInteger additionalService1Volume;
    private BigDecimal additionalService1Value;

    private BigInteger additionalService2Volume;
    private BigDecimal additionalService2Value;

    private BigInteger airtimeRechargeVolume;
    private BigDecimal airtimeRechargeValue;

    private BigInteger billPaymentVolume;
    private BigDecimal billPaymentValue;

    private BigInteger bvnEnrollmentVolume;
    private BigDecimal bvnEnrollmentValue;

    private BigInteger cashInVolume;
    private BigDecimal cashInValue;

    private BigInteger cashOutVolume;
    private BigDecimal cashOutValue;

    private BigInteger fundTransferVolume;
    private BigDecimal fundTransferValue;

    private BigInteger othersVolume;
    private BigDecimal othersValue;

    public Dashboard(BigInteger accountOpeningVolume, BigDecimal accountOpeningValue, BigInteger additionalService1Volume,
                     BigDecimal additionalService1Value, BigInteger additionalService2Volume, BigDecimal additionalService2Value,
                     BigInteger airtimeRechargeVolume, BigDecimal airtimeRechargeValue, BigInteger billPaymentVolume,
                     BigDecimal billPaymentValue, BigInteger bvnEnrollmentVolume, BigDecimal bvnEnrollmentValue,
                     BigInteger cashInVolume, BigDecimal cashInValue, BigInteger cashOutVolume, BigDecimal cashOutValue,
                     BigInteger fundTransferVolume, BigDecimal fundTransferValue, BigInteger othersVolume, BigDecimal othersValue) {

        this.accountOpeningVolume = accountOpeningVolume == null ? BigInteger.ZERO : accountOpeningVolume;
        this.accountOpeningValue = accountOpeningValue == null ? BigDecimal.ZERO : accountOpeningValue;
        this.additionalService1Volume = additionalService1Volume == null ? BigInteger.ZERO : additionalService1Volume;
        this.additionalService1Value = additionalService1Value == null ? BigDecimal.ZERO : additionalService1Value;
        this.additionalService2Volume = additionalService2Volume == null ? BigInteger.ZERO : additionalService2Volume;
        this.additionalService2Value = additionalService2Value == null ? BigDecimal.ZERO : additionalService2Value;
        this.airtimeRechargeVolume = airtimeRechargeVolume == null ? BigInteger.ZERO : airtimeRechargeVolume;
        this.airtimeRechargeValue = airtimeRechargeValue == null ? BigDecimal.ZERO : airtimeRechargeValue;
        this.billPaymentVolume = billPaymentVolume == null ? BigInteger.ZERO : billPaymentVolume;
        this.billPaymentValue = billPaymentValue == null ? BigDecimal.ZERO : billPaymentValue;
        this.bvnEnrollmentVolume = bvnEnrollmentVolume == null ? BigInteger.ZERO : bvnEnrollmentVolume;
        this.bvnEnrollmentValue = bvnEnrollmentValue == null ? BigDecimal.ZERO : bvnEnrollmentValue;
        this.cashInVolume = cashInVolume == null ? BigInteger.ZERO : cashInVolume;
        this.cashInValue = cashInValue == null ? BigDecimal.ZERO : cashInValue;
        this.cashOutVolume = cashOutVolume == null ? BigInteger.ZERO : cashOutVolume;
        this.cashOutValue = cashOutValue == null ? BigDecimal.ZERO : cashOutValue;
        this.fundTransferVolume = fundTransferVolume == null ? BigInteger.ZERO : fundTransferVolume;
        this.fundTransferValue = fundTransferValue == null ? BigDecimal.ZERO : fundTransferValue;
        this.othersVolume = othersVolume == null ? BigInteger.ZERO : othersVolume;
        this.othersValue = othersValue == null ? BigDecimal.ZERO : othersValue;

        this.totalVolume = totalVolume.add(this.accountOpeningVolume).add(this.additionalService1Volume).add(this.additionalService2Volume)
                .add(this.airtimeRechargeVolume).add(this.billPaymentVolume).add(this.bvnEnrollmentVolume).add(this.cashInVolume).add(this.cashOutVolume)
                .add(this.fundTransferVolume).add(this.othersVolume);

        this.totalValue = totalValue.add(this.accountOpeningValue).add(this.additionalService1Value).add(this.additionalService2Value)
                .add(this.airtimeRechargeValue).add(this.billPaymentValue).add(this.bvnEnrollmentValue).add(this.cashInValue).add(this.cashOutValue)
                .add(this.fundTransferValue).add(this.othersValue);
    }
}
