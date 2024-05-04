/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.BillingDto;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.dto.TransactionDetail;
import ng.upperlink.nibss.cmms.dto.TransactionSummaryDto;
import ng.upperlink.nibss.cmms.enums.*;
import ng.upperlink.nibss.cmms.model.Transaction;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.auth.Role;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.Biller;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.mandate.Fee;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.model.pssp.PsspUser;
import ng.upperlink.nibss.cmms.repo.TransactionRepo;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import ng.upperlink.nibss.cmms.service.pssp.PsspUserService;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.util.stream.Collectors.groupingBy;

/**
 *
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@Transactional
public class TransactionService {
    
    private final TransactionRepo transactionRepo;
    private final BillerUserService  billerUserService;
    private final BankUserService bankUserService;
    private final PsspUserService psspUserService;
    
    @Value("${cmms.bank.suffix}")
    private String sortCodeSuffix;

    @Value("${cmms.smartdet.code}")
    private String smartDetCode;
    
    @Autowired
    public TransactionService(TransactionRepo transactionRepo, BillerUserService billerUserService, BankUserService bankUserService, PsspUserService psspUserService) {
        this.transactionRepo = transactionRepo;
        this.billerUserService = billerUserService;
        this.bankUserService = bankUserService;
        this.psspUserService = psspUserService;
    }
    
    public Transaction getTransactionById(Long transactionId) {
        try {
            return transactionRepo.findOne(transactionId);
        } catch (Exception e) {
            log.error("Unable to fetch transaction with ID {}", transactionId, e);
        }
        return null;
    }
    
    public List<Transaction> getTransactionBilling(Date startDate, Date endDate) {
        return transactionRepo.getTransactionsBilling(TransactionStatus.PAYMENT_SUCCESSFUL, startDate, endDate);
    }
    
    public List<BillingDto> getBanksBilling(List<? extends Transaction> transactions) {
        Map<String, List<Transaction>> tranxBySubscriberBanks = transactions.parallelStream().collect(groupingBy(t -> t.getMandate().getBank().getCode()));
        List<BillingDto> billing = new ArrayList<>();
        
        // get the sum per bank
        String dateColumn = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date());
        tranxBySubscriberBanks.forEach((k, v) -> {
            BigDecimal sum = v.parallelStream().map(t -> t.getFee()).reduce((a,b) -> a.add(b)).orElseGet(() -> BigDecimal.ZERO);
            billing.add(new BillingDto(String.format("%s%s", k, sortCodeSuffix), smartDetCode, dateColumn, sum.toPlainString()));
        });
        return billing;
    }
    
    public List<Transaction> getTransactionWeeklyBilling(Date startDate, Date endDate) {
        return transactionRepo.getTransactionsBillingByDate(TransactionStatus.PAYMENT_SUCCESSFUL, startDate, endDate);
    }
    
    public List<NIBSSPayPayment> getNIBSSPaymentBilling(Date startDate, Date endDate) {
        return transactionRepo.getNIBSSPayPayment(TransactionStatus.PAYMENT_SUCCESSFUL, startDate, endDate);
    }
    public List<NIBSSPayPayment> getNIBSSPaymentWeeklyBilling(Date startDate, Date endDate) {
        return transactionRepo.getNIBSSPayPaymentByDate(TransactionStatus.PAYMENT_SUCCESSFUL, startDate, endDate);
    }
    
    public List<NIBSSPayPayment> getNIBSSDebitPaymentWeeklyBilling(Date startDate, Date endDate) {
        return transactionRepo.getBeneficiaryWeeklyBilling(TransactionStatus.PAYMENT_SUCCESSFUL, startDate, endDate);
    }
    
    public synchronized Transaction saveTransaction(Transaction transaction) {
        try {
            return transactionRepo.saveAndFlush(transaction);
        } catch (Exception e) {
            log.error("An exception occured while trying to save transaction: {}", transaction.getMandate().getMandateCode(), e);
        }
        return null;
    }

    
    @Async
    public void updateSelectedTransactions(List<? extends Transaction> transactions) {
        transactions.stream().forEach(t -> t.setBilled(true));
        transactionRepo.save(transactions);
    }
    
    public void saveSynchronousTransaction(Transaction transaction) {
        try {
            transactionRepo.saveAndFlush(transaction);
        } catch (Exception e) {
            log.error("An exception occured while tryng to save transaction: {}", transaction.getMandate().getMandateCode(), e);
        }
    }
    
    public List<Transaction> getFreshTransactions() {
        try {
            return transactionRepo.getTransactions(TransactionStatus.PAYMENT_ENTERED, 0, 0);
        } catch (Exception e) {
            log.error("An exception occurred while trying to fetch fresh transactions", e);
        }
        return new ArrayList<>();
    }
    
    public List<Transaction> getPreviousDaysTransactions(Date date, int days, int numberOfRetrials, int frequency) {
        Date failureDate = CommonUtils.subtractDays(date, days);
        try {
            return transactionRepo.getPreviousDaysTransactions(TransactionStatus.PAYMENT_SUCCESSFUL, numberOfRetrials, failureDate, frequency);
        } catch (Exception e) {
            log.error("Unable to retrieve previous days transactions for {}", days, e);
        }
        return new ArrayList<>();
    }
    
    /**
     * Get all transaction summary details
     * @param status
     * @return 
     */
    public Future<List<TransactionSummaryDto>> getAllTransactionsSummary(TransactionStatus status, Date date) {
        try {
            return transactionRepo.getAllTransactions(status, date);
        } catch (Exception e) {
            log.error("Unable to fetch all transactions summary", e);
        }
        return null;
    }
    
    /**
     * Get Transaction summary by biller
     * @param biller
     * @param status
     * @return 
     */
    public Future<List<TransactionSummaryDto>> getTransactionSummaryByBiller(Biller biller, TransactionStatus status, Date date) {
        try {
            return transactionRepo.getAllTransactionsByBiller(status, biller, date);
        } catch (Exception e) {
            log.error("Unable to fetch transaction summary for the biller {}", biller.getName(), e);
        }
        return null;
    }
    
    /**
     * Get Transaction Summary by owner
     * @param billerOwner
     * @param status
     * @return 
     */
    public Future<List<TransactionSummaryDto>> getTransactionSummaryByBillerOwner(String billerOwner, TransactionStatus status, Date date) {
        try {
            return transactionRepo.getAllTransactionsByBillerOwner(status, billerOwner, date);
        } catch (Exception e) {
            log.error("Unable to fetch transaction summary details by biller owner {}", billerOwner, e);
        }
        return null;
    }
    
    /**
     * Get Transaction Summary by Roles
     * @param user
     * @param status
     * @return 
     */
    public Future<List<TransactionSummaryDto>> getTransactionSummaryByRole(User user, Supplier<TransactionStatus> status, Supplier<Date> date) {
        Set<Role> roles = user.getRoles();
        if (null == roles || roles.isEmpty())
            throw new RuntimeException("There is no role profiled for this user");
        else {
            Role role = roles.iterator().next();
            if (role.getName().getValue().contains("NIBSS")) {
                return getAllTransactionsSummary(status.get(), date.get());
            } else if (role.getName().getValue().contains("BANK")) {
                return getTransactionSummaryByBillerOwner(getBillerOwner(BillerOwner.BANK, user), status.get(), date.get());
            } else if (role.getName().getValue().contains("BILLER")) {
                BillerUser billerUser = billerUserService.getUserById(user.getId());
                if (null == billerUser)
                    throw new RuntimeException("This user is not mapped to any biller");
                return getTransactionSummaryByBiller(billerUser.getBiller(), status.get(), date.get());
            } else if (role.getName() == RoleName.PSSP_ADMIN_INITIATOR  || role.getName() == RoleName.PSSP_ADMIN_AUTHORIZER) {
                return getTransactionSummaryByBillerOwner(getBillerOwner(BillerOwner.PSSP, user), status.get(), date.get());
            } else 
                throw new RuntimeException("Unable to identify the role of this user");
        }
    }
    
    public Page<TransactionDetail> processTransactionDetails(User user, Supplier<TransactionStatus> status, Date startDate, Date endDate, Pageable pageable) {
        Set<Role> roles = user.getRoles();
        if (null == roles || roles.isEmpty())
            throw new RuntimeException("There is no profiled for user");
        else {
            Role role = roles.iterator().next();
            if (role.getName().getValue().contains("NIBSS")) { // this is a NIBSS user
                processAllTransactionDetails(status.get(), startDate, endDate, pageable);
            } else if (role.getName().getValue().contains("BANK")) { // this a bank user
                return processTransactionByBillerOwner(getBillerOwner(BillerOwner.BANK, user), status.get(), startDate, endDate, pageable);
            } else if (role.getName() == RoleName.PSSP_ADMIN_INITIATOR || role.getName() == RoleName.PSSP_ADMIN_AUTHORIZER) {
                return processTransactionByBillerOwner(getBillerOwner(BillerOwner.PSSP, user), status.get(), startDate, endDate, pageable);
            } else if (role.getName().getValue().contains("BILLER")) {
                BillerUser billerUser = billerUserService.getUserById(user.getId());
                if (null == billerUser)
                    throw new RuntimeException("This user is not mapped to any biller");
                return processTransactionsByBiller(billerUser.getBiller(), status.get(), startDate, endDate, pageable);
            } else 
                throw new RuntimeException("Unable to identify the role of this user");
        }
        return null;
    }
    
    /**
     * Get all transaction details for users with ADMIN role
     * @param status
     * @param startDate
     * @param endDate
     * @param pageable
     * @return 
     */
    private Page<TransactionDetail> processAllTransactionDetails(TransactionStatus status, Date startDate, Date endDate, Pageable pageable) {
        try {
            if (null != status) {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getAllTransactionDetailsByStatusAndDate(status, startDate, endDate, pageable);
                } else {
                    return transactionRepo.getAllTransactionDetailsByStatus(status, pageable);
                }
            } else {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getAllTransactionDetailsByDate(startDate, endDate, pageable);
                } else {
                    return transactionRepo.getAllTransactionDetails(pageable);
                }
            }
        } catch (Exception e) {
            log.error("An exception occurred while trying to fetch all transaction details", e);
        }
        return null;
    }
    
    private Page<TransactionDetail> processTransactionByBillerOwner(String apiKey, TransactionStatus status, Date startDate, Date endDate, Pageable pageable) {
        try {
            if (null != status) {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getTransactionDetailsByBillerOwnerAndStatusAndDate(apiKey, status, startDate, endDate, pageable);
                } else {
                    return transactionRepo.getTransactionDetailsByBillerOwnerAndStatus(apiKey, status, pageable);
                }
            } else {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getTransactionDetailsByBillerOwnerAndDate(apiKey, startDate, endDate, pageable);
                } else {
                    return transactionRepo.getTransactionDetailsByBillerOwner(apiKey, pageable);
                }
            }
        } catch (Exception e) {
            log.error("An exception occurred while trying to fetch transaction details by biller owner: {}", apiKey, e);
        }
        return null;
    }
    
    private Page<TransactionDetail> processTransactionsByBiller(Biller biller, TransactionStatus status, Date startDate, Date endDate, Pageable pageable) {
        try {
            if (null != status) {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getTransactionDetailsByBillerAndStatusAndDate(biller, status, startDate, endDate, pageable);
                } else {
                    return transactionRepo.getTransactionDetailsByBillerAndStatus(biller, status, pageable);
                }
            } else {
                if (null != startDate && null != endDate) {
                    return transactionRepo.getTransactionDetailsByBillerAndDate(biller, startDate, endDate, pageable);
                } else {
                    return transactionRepo.getTransactionDetailsByBiller(biller, pageable);
                }
            }
        } catch (Exception e) {
            log.error("An exception occurred while trying to fetch transaction details by biller: {}", biller.getName(), e);
        }
        return null;
    }
    
    private String getBillerOwner(BillerOwner billerOwner, User user) {
        switch (billerOwner) {
            case BANK:
                BankUser bankUser = bankUserService.getById(user.getId());
                return bankUser.getUserBank().getApiKey();
            case PSSP:
                PsspUser psspUser = psspUserService.getById(user.getId());
                return psspUser.getPssp().getApiKey();
            default:
                throw new RuntimeException("Invalid biller owner provided");
        }
    }
    
    @Async
    public CompletableFuture<Transaction> processTransaction(Mandate mandate, Channel transactionChannel) {
        // calculate the fees
        log.info("Entered Process");
        BigDecimal fee = calculateFee(mandate);
        // add to the transactions table
        Transaction transaction = buildTransaction(mandate, fee, transactionChannel);
        // save transaction details
        Transaction savedTransaction = saveTransaction(transaction);
        return CompletableFuture.completedFuture(savedTransaction);
    }
    
    private synchronized BigDecimal calculateFee(Mandate mandate) {
        log.info("Fee method");
        Biller biller = mandate.getBiller();
        if (null == biller) {
            log.error("Unable to fetch the biller for the mandate with code {}", mandate.getMandateCode());
            return BigDecimal.ZERO;
        }

        Fee fee = biller.getFee().iterator().next();
        if (null == fee) {
            log.error("There is no fee configured for this biller with ID {}", biller.getId());
            return BigDecimal.ZERO;
        }
        return calculateFeeByBearer(fee.getSplitType(), fee, mandate.isFixedAmountMandate() ? mandate.getAmount() : mandate.getVariableAmount());
    }
    
    private synchronized BigDecimal calculateFeeByBearer(SplitType splitType, Fee fee, BigDecimal amount) {
        switch(splitType) {
            case NONE:
                return fee.getDefaultFee();
            case FIXED:
                return fee.getDefaultFee().add(fee.getMarkUpFee());
            case PERCENTAGE:
                BigDecimal percentageValue = null == fee.getPercentageAmount() ? BigDecimal.ZERO : fee.getPercentageAmount().divide(new BigDecimal(100)); // This means that the percentage value will be a non-decimal value
                BigDecimal feeAmount = amount.multiply(percentageValue);
                return fee.getDefaultFee().add(feeAmount);
            default:
                return fee.getDefaultFee();
        }
    }
    
    private synchronized Transaction buildTransaction(Mandate m, BigDecimal fee, Channel channel) {
        Transaction transaction = new Transaction();
        Fee billerFee = m.getBiller().getFee().iterator().next();
        transaction.setMandate(m);
        transaction.setAmount(m.isFixedAmountMandate() ? m.getAmount() : m.getVariableAmount()); // determine the nature of mandate
        transaction.setBearer(billerFee.getFeeBearer());
        transaction.setSplitType(billerFee.getSplitType());
        transaction.setBillableAtTransactionTime(billerFee.isDebitAtTransactionTime());
        transaction.setDefaultFee(billerFee.getDefaultFee());
        transaction.setFee(fee);
        transaction.setMarkedUp(billerFee.isMarkedUp());
        transaction.setBank(billerFee.getBank());
        transaction.setAccountName(billerFee.getAccountName());
        transaction.setAccountNumber(billerFee.getAccountNumber());
        transaction.setBillerDebitAccountName(billerFee.getBillerDebitAccountName());
        transaction.setBillerDebitAccountNumber(billerFee.getBillerDebitAccountNumber());
        transaction.setBillerDebitBank(billerFee.getBillerDebitBank());
        transaction.setDateCreated(new Date());
        transaction.setPaymentDate(new Date());
        transaction.setTransactionType(channel);
        transaction.setStatus(TransactionStatus.PAYMENT_ENTERED);
        return transaction;
    }

    /**
     * Get Transaction by mandate code
     * @param mandateId
     * @return
     */
    public Transaction getTransactionByMandateId(Long mandateId)
    {
        return transactionRepo.getTransactionByMandate(mandateId);
    }
}
