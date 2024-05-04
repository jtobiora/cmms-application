/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.groupingBy;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Service 
@Slf4j
public class BillingService implements BillingProvider {
    private final BillingHelper billingHelper;
    
    
    @Autowired
    public BillingService(BillingHelper billingHelper) {
        this.billingHelper = billingHelper;
    }

    /** {@inheritDoc} */
    @Override
    public Path getBillingZipFile(List<? extends Transaction> transactions, List<NIBSSPayPayment> nibssPaymentTransactions, String billingPath, BillingPeriod billingPeriod) throws RuntimeException, IOException {
        // create billing folder if it does not exist
        billingHelper.createBillingFolder(billingPath);
        try {
            CompletableFuture<String> smartDetFile = getSmartDetFile(transactions);
            CompletableFuture<String> nibssPayFile = getNIBSSPayPaymentFile(nibssPaymentTransactions);
            CompletableFuture<String> transactionDetailsFile = getTransactionDetails(transactions);
            
            // wait for all process to complete
            CompletableFuture.allOf(smartDetFile, nibssPayFile, transactionDetailsFile).join();
            
            String zipFileDate = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.ENGLISH).format(new Date());
            String zipFileName = String.format("CMMSBilling_%s_%s.zip", zipFileDate, billingPeriod);
            
            Path cmmsZipBillingPath = Paths.get(billingPath, zipFileName);
            Map<String, String> filesMap = new HashMap<>();
            
            filesMap.put(String.format("CMMSSmartDet_%s.csv", zipFileDate), smartDetFile.get());
            
            String nibssPaymentFile = nibssPayFile.get();
            if (null != nibssPaymentFile && !nibssPaymentFile.trim().isEmpty())
                filesMap.put(String.format("CMMSNIBSSPaymentFile_%s.csv", zipFileDate), nibssPaymentFile);
            
            filesMap.put(String.format("TransactionDetails_%s.csv", zipFileDate), transactionDetailsFile.get());
            
            // write zip file to disc
            billingHelper.writeZipPaymentFile(filesMap, cmmsZipBillingPath);
            log.trace("Done generating zip payment file");
            
            return cmmsZipBillingPath;
        } catch (InterruptedException e) {
            log.error("The process for smart DET and NIBSS Pay was interrupted", e);
            throw new RuntimeException("The process for smart DET and NIBSS Pay was interrupted");
        } catch(ExecutionException e) {
            log.error("Execution exception occurred while process smart DET file and NIBSSPAY file", e);
            throw new RuntimeException("Execution exception occurred while process smart det file and NIBSSPAY file");
        }
    }

    /** {@inheritDoc} */
    @Async
    @Override
    public CompletableFuture<String> getNIBSSPayPaymentFile(List<NIBSSPayPayment> transactions) {
        if (null == transactions || transactions.isEmpty())
            return CompletableFuture.completedFuture(null);
        String nibssPayFile = billingHelper.getNIBSSPayPaymentFile(transactions);
        return CompletableFuture.completedFuture(nibssPayFile);
    }

    /** {@inheritDoc} */
    @Async
    @Override
    public CompletableFuture<String> getSmartDetFile(List<? extends Transaction> transactions) {
        // group transactions by subscriber's bank
        Map<String, List<Transaction>> tranxBySubscriberBanks = transactions.parallelStream().collect(groupingBy(t -> t.getMandate().getBank().getCode()));
        Map<String, BigDecimal> smartDetMap = new HashMap<>();
        
        // get the sum per bank
        tranxBySubscriberBanks.forEach((k, v) -> {
            BigDecimal sum = v.parallelStream().map(t -> t.getFee()).reduce((a,b) -> a.add(b)).orElseGet(() -> BigDecimal.ZERO);
            smartDetMap.put(k, sum);
        });
        
        String smartDetFile = billingHelper.getSmartDetFile(smartDetMap);
        return CompletableFuture.completedFuture(smartDetFile);
    }

    /** {@inheritDoc} */
    @Override
    public void cleanUp(List<? extends Transaction> transactions, List<NIBSSPayPayment> nibssPaymentTransactions) {
        transactions.clear();
        nibssPaymentTransactions.clear();
    }

    /** {@inheritDoc} */
    @Async
    @Override
    public CompletableFuture<String> getTransactionDetails(List<? extends Transaction> transactions) {
        String transactionFile = billingHelper.getTransactionsFile(transactions);
        return CompletableFuture.completedFuture(transactionFile);
    }
    
}
