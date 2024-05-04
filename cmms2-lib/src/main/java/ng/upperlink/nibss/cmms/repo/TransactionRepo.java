/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.repo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.dto.TransactionDetail;
import ng.upperlink.nibss.cmms.dto.TransactionSummaryDto;
import ng.upperlink.nibss.cmms.enums.TransactionStatus;
import ng.upperlink.nibss.cmms.model.Transaction;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.status = ?1 AND t.numberOfTrials = ?2 AND t.mandate.frequency != ?3")
    List<Transaction> getTransactions(TransactionStatus status, int numberOfTrials, int frequency);
    @Query("SELECT t FROM Transaction t WHERE t.status != ?1 AND t.numberOfTrials = ?2 AND t.dateCreated >= ?3 AND t.mandate.frequency != ?4")
    List<Transaction> getPreviousDaysTransactions(TransactionStatus transactionStatus, int numberOfTrials, Date date, int frequency);

    // get approved transactions by date
    @Query("SELECT t FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = true AND t.billed = false")
    List<Transaction> getTransactionsBillingByDate(TransactionStatus status, Date startDate, Date endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = true")
    List<Transaction> getTransactionsBilling(TransactionStatus status, Date startDate, Date endDate);
    
    // get NIBSS Pay transactions by date
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.NIBSSPayPayment(t.accountNumber, t.accountName, t.bank.code, "
            + "SUM(t.fee - t.defaultFee))"
            + " FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = true AND t.markedUp = true  AND t.billed = false "
            + "GROUP BY t.accountNumber, t.accountName, t.bank.code")
    List<NIBSSPayPayment> getNIBSSPayPaymentByDate(TransactionStatus status, Date startDate, Date endDate);
    
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.NIBSSPayPayment(t.accountNumber, t.accountName, t.bank.code, "
            + "SUM(t.fee - t.defaultFee))"
            + " FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = true AND t.markedUp = true "
            + "GROUP BY t.accountNumber, t.accountName, t.bank.code")
    List<NIBSSPayPayment> getNIBSSPayPayment(TransactionStatus status, Date startDate, Date endDate);
    
    // Generate Biller Markup File
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.NIBSSPayPayment(t.billerDebitAccountNumber, t.billerDebitAccountName, t.billerDebitBank.code, "
            + "SUM(t.defaultFee))"
            + " FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = false  AND t.billed = false "
            + "GROUP BY t.billerDebitAccountNumber, t.billerDebitAccountName, t.billerDebitBank.code")
    List<NIBSSPayPayment> getBeneficiaryWeeklyBilling(TransactionStatus status, Date startDate, Date endDate);
    
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.NIBSSPayPayment(t.billerDebitAccountNumber, t.billerDebitAccountName, t.billerDebitBank.code, "
            + "SUM(t.defaultFee))"
            + " FROM Transaction t WHERE t.status = ?1 AND t.paymentDate BETWEEN ?2 AND ?3 AND t.billableAtTransactionTime = false "
            + "GROUP BY t.billerDebitAccountNumber, t.billerDebitAccountName, t.billerDebitBank.code")
    List<NIBSSPayPayment> getBeneficiaryBilling(TransactionStatus status, Date startDate, Date endDate);
    
    // get transaction summary for NIBSS user
    @Async
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.TransactionSummaryDto(t.mandate.biller.id, t.mandate.biller.name, SUM(CASE WHEN (t.status = ?1) THEN t.amount ELSE 0 END), "
            + "SUM(CASE WHEN (t.status = ?1) THEN 1 ELSE 0 END))"
            + " FROM Transaction t WHERE t.dateCreated >= ?2 GROUP BY t.mandate.biller.id, t.mandate.biller.name ORDER BY t.mandate.biller.name")
    CompletableFuture<List<TransactionSummaryDto>> getAllTransactions(TransactionStatus status, Date date);
    
    // get transaction summary by biller
    @Async
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.TransactionSummaryDto(t.mandate.product.id, t.mandate.product.name, SUM(CASE WHEN (t.status = ?1) THEN t.amount ELSE 0 END), "
            + "SUM(CASE WHEN (t.status = ?1) THEN 1 ELSE 0 END))"
            + " FROM Transaction t WHERE t.mandate.biller = ?2 AND t.dateCreated >= ?3 GROUP BY t.mandate.product.id, t.mandate.product.name  ORDER BY t.mandate.product.name")
    CompletableFuture<List<TransactionSummaryDto>> getAllTransactionsByBiller(TransactionStatus status, Biller biller, Date date);
    
    // get transaction summary by biller owner
    @Async
    @Query("SELECT new ng.upperlink.nibss.cmms.dto.TransactionSummaryDto(t.mandate.biller.id, t.mandate.biller.name, SUM(CASE WHEN (t.status = ?1) THEN t.amount ELSE 0 END), "
            + "SUM(CASE WHEN (t.status = ?1) THEN 1 ELSE 0 END))"
            + " FROM Transaction t WHERE t.mandate.biller.apiKey = ?2 AND t.dateCreated >= ?3 GROUP BY t.mandate.biller.id, t.mandate.biller.name ORDER BY t.mandate.biller.name")
    CompletableFuture<List<TransactionSummaryDto>> getAllTransactionsByBillerOwner(TransactionStatus status, String apiKey, Date date);
    
    /**
     * Transaction Details
     */
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t")
    Page<TransactionDetail> getAllTransactionDetails(Pageable pageable);
    
    // Transaction Details by Date 
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.dateCreated BETWEEN ?1 AND ?2 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.dateCreated BETWEEN ?1 AND ?2")
    Page<TransactionDetail> getAllTransactionDetailsByDate(Date startDate, Date endDate, Pageable pageable);
    
    // Transaction Details by Status
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.status = ?1 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.status = ?1")
    Page<TransactionDetail> getAllTransactionDetailsByStatus(TransactionStatus status, Pageable pageable);
    
    // Transaction Details By Status and Date
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.status = ?1 AND t.dateCreated BETWEEN ?2 AND ?3 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.status = ?1 AND t.dateCreated BETWEEN ?2 AND ?3")
    Page<TransactionDetail> getAllTransactionDetailsByStatusAndDate(TransactionStatus status, Date startDate, Date endDate, Pageable pageable);
    
    // Biller Transaction Details
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller = ?1 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller = ?1")
    Page<TransactionDetail> getTransactionDetailsByBiller(Biller biller, Pageable pageable);
    
    // Biller Transaction Details By Date
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller = ?1 AND t.dateCreated BETWEEN ?2 and ?3 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller = ?1")
    Page<TransactionDetail> getTransactionDetailsByBillerAndDate(Biller biller, Date startDate, Date endDate, Pageable pageable);
    
    // Biller Transaction Details by Status
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller = ?1 AND t.status = ?2 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller = ?1 AND t.status = ?2")
    Page<TransactionDetail> getTransactionDetailsByBillerAndStatus(Biller biller, TransactionStatus status, Pageable pageable);
    
    // Biller Transaction Details by Status and Date
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller = ?1 AND t.status = ?2 AND t.dateCreated BETWEEN ?3 AND ?4 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller = ?1 AND t.status = ?2 AND t.dateCreated BETWEEN ?3 AND ?4")
    Page<TransactionDetail> getTransactionDetailsByBillerAndStatusAndDate(Biller biller, TransactionStatus status, Date startDate, Date endDate, Pageable pageable);
    
    // Biller Owner Transaction Details
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller.billerOwner = ?1 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1")
    Page<TransactionDetail> getTransactionDetailsByBillerOwner(String apiKey, Pageable pageable);
    
    // Biller Owner Transaction Details by Date
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1 AND t.dateCreated BETWEEN ?2 and ?3 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1")
    Page<TransactionDetail> getTransactionDetailsByBillerOwnerAndDate(String apiKey, Date startDate, Date endDate, Pageable pageable);
    
    // Biller Owner Transaction Details by Status
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1 AND t.status = ?2 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1 AND t.status = ?2")
    Page<TransactionDetail> getTransactionDetailsByBillerOwnerAndStatus(String apiKey, TransactionStatus status, Pageable pageable);
    
    // Biller Owner Transaction Details by Status and Date
    @Query(value = "SELECT new ng.upperlink.nibss.cmms.dto.TransactionDetail(t.id, t.fee, t.amount, t.billableAtTransactionTime,"
            + "t.mandate.mandateCode, t.transactionType, t.numberOfCreditTrials, t.numberOfTrials, t.dateCreated, "
            + "t.lastDebitDate, t.lastCreditDate, t.status) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1 AND t.status = ?2 AND t.dateCreated BETWEEN ?3 AND ?4 ORDER BY t.id DESC",
            countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.mandate.biller.apiKey = ?1 AND t.status = ?2 AND t.dateCreated BETWEEN ?3 AND ?4")
    Page<TransactionDetail> getTransactionDetailsByBillerOwnerAndStatusAndDate(String apiKey, TransactionStatus status, Date startDate, Date endDate, Pageable pageable);

    /**
     * Get Transaction by mandat Id
     * @param mandateId
     * @return
     */
    @Query("SELECT t FROM Transaction t WHERE t.mandate.id =:mandateId")
    Transaction getTransactionByMandate(@Param("mandateId") Long mandateId);
}
