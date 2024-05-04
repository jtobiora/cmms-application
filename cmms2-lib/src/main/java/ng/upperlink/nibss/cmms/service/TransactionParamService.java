/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.model.Transaction;
import ng.upperlink.nibss.cmms.model.TransactionParam;
import ng.upperlink.nibss.cmms.repo.TransactionParamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Service
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransactionParamService {
    
    @Autowired
    TransactionParamRepo transactionParamRepo;
    
    public void save(TransactionParam transactionParam) {
        try {
            transactionParamRepo.saveAndFlush(transactionParam);
        } catch (Exception e) {
            log.error("An excepiton occurred while trying to save transactions param for the debit and credit operations", e);
        }
    }
    
    public TransactionParam buildTransactionParam(Supplier<Map<String, Object>> params) {
        TransactionParam transactionParam = new TransactionParam();
        transactionParam.setCreditResponseCode((String) params.get().get("creditResponseCode"));
        transactionParam.setDateCreated(new Timestamp(System.currentTimeMillis()));
        transactionParam.setDebitResponseCode((String) params.get().get("debitResponseCode"));
        transactionParam.setSessionId((String) params.get().get("sessionId"));
        transactionParam.setTransaction((Transaction) params.get().get("transaction"));
        transactionParam.setAmountCredited((BigDecimal) params.get().get("amountCredited"));
        transactionParam.setAmountDebited((BigDecimal) params.get().get("amountDebited"));
        return transactionParam;
    }
}
