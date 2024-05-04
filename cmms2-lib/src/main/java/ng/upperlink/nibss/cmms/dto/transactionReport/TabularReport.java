package ng.upperlink.nibss.cmms.dto.transactionReport;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


@Data
@NoArgsConstructor
public class TabularReport extends Dashboard {

    private Long id;
    private String name;
    private String code;

/*    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date createdAt;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date updatedAt;

    @JsonIgnore
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date transactionDate;*/

    public TabularReport(BigInteger accountOpeningVolume, BigDecimal accountOpeningValue, BigInteger additionalService1Volume, BigDecimal additionalService1Value, BigInteger additionalService2Volume, BigDecimal additionalService2Value, BigInteger airtimeRechargeVolume, BigDecimal airtimeRechargeValue, BigInteger billPaymentVolume, BigDecimal billPaymentValue,
                         BigInteger bvnEnrollmentVolume, BigDecimal bvnEnrollmentValue, BigInteger cashInVolume, BigDecimal cashInValue, BigInteger cashOutVolume, BigDecimal cashOutValue, BigInteger fundTransferVolume, BigDecimal fundTransferValue, BigInteger othersVolume, BigDecimal othersValue,
                         Long id, String name, String code, Date createdAt, Date updatedAt, Date transactionDate) {
        super(accountOpeningVolume, accountOpeningValue, additionalService1Volume, additionalService1Value, additionalService2Volume, additionalService2Value, airtimeRechargeVolume, airtimeRechargeValue, billPaymentVolume, billPaymentValue, bvnEnrollmentVolume, bvnEnrollmentValue, cashInVolume, cashInValue, cashOutVolume, cashOutValue, fundTransferVolume, fundTransferValue, othersVolume, othersValue);
        this.id = id;
        this.name = name;
        this.code = code;
/*        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionDate = transactionDate;*/
    }

    public TabularReport(BigInteger accountOpeningVolume, BigDecimal accountOpeningValue, BigInteger additionalService1Volume, BigDecimal additionalService1Value, BigInteger additionalService2Volume, BigDecimal additionalService2Value, BigInteger airtimeRechargeVolume, BigDecimal airtimeRechargeValue, BigInteger billPaymentVolume, BigDecimal billPaymentValue,
                         BigInteger bvnEnrollmentVolume, BigDecimal bvnEnrollmentValue, BigInteger cashInVolume, BigDecimal cashInValue, BigInteger cashOutVolume, BigDecimal cashOutValue, BigInteger fundTransferVolume, BigDecimal fundTransferValue, BigInteger othersVolume, BigDecimal othersValue,
                         Long id, String name, String code) {
        super(accountOpeningVolume, accountOpeningValue, additionalService1Volume, additionalService1Value, additionalService2Volume, additionalService2Value, airtimeRechargeVolume, airtimeRechargeValue, billPaymentVolume, billPaymentValue, bvnEnrollmentVolume, bvnEnrollmentValue, cashInVolume, cashInValue, cashOutVolume, cashOutValue, fundTransferVolume, fundTransferValue, othersVolume, othersValue);
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
