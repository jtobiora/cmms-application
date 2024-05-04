/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.model.Transaction;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public interface BillingProvider {
    static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    
    /**
     * The different billing periods that can be accomodated in the system
     */
    enum BillingPeriod {
        WEEKLY, BACKLOGS, MONTHLY, PERIOD
    }
    
    /**
     * Get Billing File
     * @param transactions
     * @param nibssPaymentTransactions
     * @param nibssDebitTransactions
     * @param startDate
     * @param endDate
     * @param billingPath
     * @param billingPeriod
     * @return the path to the entire transactions
     * @throws RuntimeException
     * @throws IOException 
     */
    Path getBillingZipFile(List<? extends Transaction> transactions, List<NIBSSPayPayment> nibssPaymentTransactions,
                                  String billingPath, BillingPeriod billingPeriod) throws RuntimeException, IOException;
    
    /**
     * Get the NIBSS Payment file
     * @param transactions
     * @param startDate
     * @param endDate
     * @return the completed file process
     */
    CompletableFuture<String> getNIBSSPayPaymentFile(List<NIBSSPayPayment> transactions);
    
    /**
     * Asynchronously process transaction details 
     * @param transactions
     * @return the generated path
     */
    CompletableFuture<String> getTransactionDetails(List<? extends Transaction> transactions);
    
    /**
     * Generate the Smart Det File
     * @param transactions
     * @return 
     */
    CompletableFuture<String> getSmartDetFile(List<? extends Transaction> transactions);
    
    /**
     * Clean the resources used
     * @param transactions
     * @param nibssPaymentTransactions 
     */
    void cleanUp(List<? extends Transaction> transactions, List<NIBSSPayPayment> nibssPaymentTransactions);
}
