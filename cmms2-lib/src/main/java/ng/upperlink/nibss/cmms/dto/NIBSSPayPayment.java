/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NIBSSPayPayment {
    private BeneficiaryAccount account;
    private BigDecimal amount;
    public NIBSSPayPayment(String accountNumber, String accountName, String bankCode, BigDecimal amount) {
        this.account = new BeneficiaryAccount(accountNumber, accountName, bankCode);
        this.amount = amount;
    }
}
