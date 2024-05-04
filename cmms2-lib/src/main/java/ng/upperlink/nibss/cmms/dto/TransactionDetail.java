/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ng.upperlink.nibss.cmms.enums.Channel;
import ng.upperlink.nibss.cmms.enums.TransactionStatus;
import ng.upperlink.nibss.cmms.util.TransactionTypeSerializer;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    private Long id;
    private BigDecimal fee;
    private BigDecimal amount;
    private boolean billableAtTransactionTime;
    private String mandateCode;
    @JsonSerialize(using = TransactionTypeSerializer.class)
    private Channel transactionType;
    private int numberOfCreditTrials;
    private int numberOfRetrials;
    private Date dateCreated;
    private Date lastDebitDate;
    private Date lastCreditDate;
    private TransactionStatus transactionStatus;
}
