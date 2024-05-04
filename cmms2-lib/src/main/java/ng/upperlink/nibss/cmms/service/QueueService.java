/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import com.nibss.nip.NipException;
import com.nibss.nip.dto.*;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.FeeBearer;
import ng.upperlink.nibss.cmms.enums.TransactionStatus;
import ng.upperlink.nibss.cmms.model.Transaction;
import ng.upperlink.nibss.cmms.model.TransactionParam;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.service.mandateImpl.MandateService;
import ng.upperlink.nibss.cmms.util.CommonUtils;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
@Transactional
public class QueueService {
    
    @Value("${nibss.channel.code}")
    private int nibssChannelCode;
    
    @Autowired
    private CmmsNipService nipService;
    @Autowired
    private MandateService mandateService;
    @Autowired
    private TransactionService transactionService;
    @Autowired 
    private TransactionParamService transactionParamService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Value("${activemq.delivery.retrial}")
    private int messageDeliveryRetrial;

    String debitResponseCode;
    String creditResponseCode;
    
    /**
     * Send message to queue
     * @param topic
     * @param payload 
     */
    public void send(String topic, String payload) {
        log.trace("Received payload for topic {} \n: {}", topic, payload);
        jmsTemplate.send(topic, (Session session) -> {
            TextMessage textMessage = session.createTextMessage(payload);
            textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, messageDeliveryRetrial * 60 * 1000);
            return textMessage;
        });
    }
    
    /**
     * Process Mandates 
     * @param payload
     * @throws JMSException 
     */
    @JmsListener(destination = "${initiate.mandate.advice.topic}", containerFactory = "containerFactory")
    public void sendMandateAdvice(final String payload) throws JMSException {
        Mandate mandate = CommonUtils.convertStringToObject(payload, Mandate.class);
        try {
            // carry out a name enquiry on the account first
            NESingleResponse nESingleResponse = nameEnquiry(mandate, Constants.DEBIT_ACTION);
            if (null == nESingleResponse || !nESingleResponse.getResponseCode().trim().equals(Constants.SUCCESSFUL))
                return;
            MandateAdviceRequest mandateAdviceRequest = new MandateAdviceRequest();
            mandateAdviceRequest.setAmount(mandate.isFixedAmountMandate() ? mandate.getAmount() : mandate.getVariableAmount());
            mandateAdviceRequest.setBeneficiaryAccountName(mandate.getBiller().getAccountName());
            mandateAdviceRequest.setBeneficiaryAccountNumber(mandate.getBiller().getAccountNumber());
    //        mandateAdviceRequest.setBeneficiaryBankVerificationNumber(manda);
            mandateAdviceRequest.setBeneficiaryKYCLevel(mandate.getBiller().getKycLevel());
            mandateAdviceRequest.setChannelCode(nibssChannelCode);
            mandateAdviceRequest.setDebitAccountName(mandate.getAccountName());
            mandateAdviceRequest.setDebitAccountNumber(mandate.getAccountNumber());
            mandateAdviceRequest.setDebitBankVerificationNumber(mandate.getBvn());
            mandateAdviceRequest.setDebitKYCLevel(nESingleResponse.getKYCLevel());
            mandateAdviceRequest.setDestinationInstitutionCode(mandate.getBank().getNipBankCode()); /** TODO: Check the actual bank code for mandate advice **/
            mandateAdviceRequest.setMandateReferenceNumber(mandate.getMandateCode());
            mandateAdviceRequest.setSessionID(CommonUtils.getSessionId(Constants.NIBSS_BANK_CODE, Constants.NIBSS_DESTINATION_CODE));
            
            MandateAdviceResponse response = nipService.doMandateAdvise(mandateAdviceRequest);
            log.trace("The response code for the mandate advice sent for the mandate {} is: {}", mandate.getMandateCode(), null != response ? response.getResponseCode() : null);
            if (null != response && null != response.getResponseCode() && response.getResponseCode().trim().equals(Constants.MANDATE_RESPONSE_SUCCESSFUL) ||
                    response.getResponseCode().trim().equals(Constants.MANDATE_RESPONSE_94) || 
                    response.getResponseCode().trim().equals(Constants.MANDATE_RESPONSE_26)) {
                // update the mandate advise column
                mandate.setMandateAdviceSent(true);
                mandate.setDateModified(new Date());
                mandateService.saveMandate(mandate);
            }
        } catch (NipException ex) {
            log.error("NIP Exception occurred while sending mandate to the bank for the code {}", mandate.getMandateCode(), ex);
        }
    }
    
    /**
     * Send Transaction to queue
     * @param transactionId
     * @param topic 
     */
    public void sendTransactionToQueue(String transactionId, String topic) {
        send(topic, transactionId);
    }
    
    /**
     * Process Transactions
     * @param payload
     * @throws JMSException 
     */
    @JmsListener(destination = "${initiate.mandate.transaction.topic}", containerFactory = "containerFactory")
    public void executeTransaction(final String payload) throws JMSException {
        processMandateTransaction(transactionService.getTransactionById(Long.valueOf(payload)));
    }
    
    /**
     * Process a transaction and returns the status
     * @param transaction
     * @return the transaction status
     */
    public synchronized TransactionStatus processMandateTransaction(Transaction transaction) {
        TransactionStatus status = TransactionStatus.PAYMENT_IN_PROGRESS;
        // update this transaction to in progress
        transaction.setLastDebitDate(new Date());
        
        final String sessionId = CommonUtils.getSessionId(Constants.NIBSS_BANK_CODE, Constants.NIBSS_DESTINATION_CODE);
        debitResponseCode = null;
        creditResponseCode = null;
        final BigDecimal debitAmount = getDebitAmount(transaction);
        final BigDecimal creditAmount = getCreditAmount(transaction);
        
        try {
            // execute subscriber account debit
            FTSingleDebitResponse debitResponse = debitSubscriber(transaction, debitAmount, sessionId);
            if (null != debitResponse && debitResponse.getResponseCode().trim().equals(Constants.SUCCESSFUL)) {
                log.trace("The debit of the subscriber with code {} and amount {} was successful", transaction.getMandate().getSubscriberCode(), debitAmount.toPlainString());
                debitResponseCode = debitResponse.getResponseCode();
                status = TransactionStatus.PAYMENT_SUCCESSFUL; /** TODO: verify that when a credit fails that the transaction is concluded as successful **/
                transaction.setPaymentDate(new Date());
                transaction.setSuccessfulSessionId(sessionId);
                
                // credit the biller
                FTSingleCreditResponse creditResponse = creditBiller(transaction, creditAmount, sessionId);
                if (null != creditResponse && creditResponse.getResponseCode().trim().equals(Constants.SUCCESSFUL)) {
                    log.trace("The biller {} was successfully credited with the amount {} for the mandate code {} and subsriber code {}", 
                            transaction.getMandate().getBiller().getName(), creditAmount.toPlainString(), transaction.getMandate().getMandateCode(), transaction.getMandate().getSubscriberCode());
                    creditResponseCode = creditResponse.getResponseCode();
                }
                else {
                    log.error("The credit of the biller {} with the amount {} for the mandate code {} and subsriber code {} failed", 
                            transaction.getMandate().getBiller().getName(), transaction.getAmount().toPlainString(), transaction.getMandate().getMandateCode(), transaction.getMandate().getSubscriberCode());
                    creditResponseCode = null == creditResponse ? Constants.SYSTEM_MALFUNCTION : creditResponse.getResponseCode();
                    /** TODO: Write the failed credit to dispute resolution **/
                }
            } else {
                log.error("The debit of the subscriber with code {} failed", transaction.getMandate().getSubscriberCode());
                status = TransactionStatus.PAYMENT_FAILED;
                debitResponseCode = null == debitResponse ? Constants.SYSTEM_MALFUNCTION : debitResponse.getResponseCode();
            }
            
            // process transaction params
            TransactionParam transactionParam = transactionParamService.buildTransactionParam(() -> {
                Map<String, Object> param = new HashMap<>();
                param.put("creditResponseCode", creditResponseCode);
                param.put("debitResponseCode", debitResponseCode);
                param.put("sessionId", sessionId);
                param.put("transaction", transaction);
                param.put("amountCredited", creditAmount);
                param.put("amountDebited", debitAmount);
                return param;
            });
            
            transactionParamService.save(transactionParam);
            
            // update this transaction
            transaction.setNumberOfTrials(transaction.getNumberOfTrials() + 1);
            transaction.setStatus(status);
            transactionService.saveSynchronousTransaction(transaction);
        } catch (NipException e) {
            log.error("An exception occurred while trying to debit subscrber and credit the biller", e);
        }
        return status; // return the status of this transaction
    }
    
    /**
     * Name enquiry
     * @param mandate
     * @param type
     * @return
     * @throws NipException 
     */
    private synchronized NESingleResponse nameEnquiry(Mandate mandate, String type) throws NipException {
        NESingleRequest nESingleRequest = new NESingleRequest();
        nESingleRequest.setSessionID(CommonUtils.getSessionId(Constants.NIBSS_BANK_CODE, Constants.NIBSS_DESTINATION_CODE));
        if (type.equals(Constants.DEBIT_ACTION)) {
            nESingleRequest.setAccountNumber(mandate.getAccountNumber());
            nESingleRequest.setChannelCode(nibssChannelCode);
            nESingleRequest.setDestinationInstitutionCode(mandate.getBank().getNipBankCode());
        } else {
            nESingleRequest.setAccountNumber(mandate.getProduct().getBiller().getAccountNumber());
            nESingleRequest.setChannelCode(nibssChannelCode);
            nESingleRequest.setDestinationInstitutionCode(mandate.getProduct().getBiller().getBank().getNipBankCode());
        }
        log.trace("The debit name enquiry request for the mandate code {}: {}", mandate.getMandateCode(), CommonUtils.convertObjectToXml(nESingleRequest));
        NESingleResponse response = nipService.doNameEnquiry(nESingleRequest);
        log.trace("The debit name enquiry response for the mandate code {}: {}", mandate.getMandateCode(), CommonUtils.convertObjectToXml(response));
        return response;
    }
    
    /**
     * Debit the subscriber account
     * @param transaction
     * @return
     * @throws NipException 
     */
    private synchronized FTSingleDebitResponse debitSubscriber(Transaction transaction, BigDecimal amount, String sessionId) throws NipException {
        NESingleResponse nESingleResponse = nameEnquiry(transaction.getMandate(), Constants.DEBIT_ACTION);
        
        if (null == nESingleResponse || !nESingleResponse.getResponseCode().trim().equals(Constants.SUCCESSFUL))
            return null;
        
        // build the fund transfer single debit request
        FTSingleDebitRequest fTSingleDebitRequest = new FTSingleDebitRequest();
        fTSingleDebitRequest.setNameEnquiryRef(nESingleResponse.getSessionID());
        fTSingleDebitRequest.setBeneficiaryAccountName(transaction.getMandate().getProduct().getBiller().getAccountName());
        fTSingleDebitRequest.setBeneficiaryBankVerificationNumber(transaction.getMandate().getProduct().getBiller().getBvn());
        fTSingleDebitRequest.setBeneficiaryAccountNumber(transaction.getMandate().getProduct().getBiller().getAccountNumber());
        fTSingleDebitRequest.setBeneficiaryKYCLevel(transaction.getMandate().getProduct().getBiller().getKycLevel());
        fTSingleDebitRequest.setChannelCode(nibssChannelCode);
        fTSingleDebitRequest.setDebitAccountName(nESingleResponse.getAccountName());
        fTSingleDebitRequest.setDebitAccountNumber(transaction.getMandate().getAccountNumber());
        fTSingleDebitRequest.setAmount(amount);
        fTSingleDebitRequest.setDebitBankVerificationNumber(nESingleResponse.getBankVerificationNumber());
        fTSingleDebitRequest.setDebitKYCLevel(nESingleResponse.getKYCLevel());
        fTSingleDebitRequest.setDestinationInstitutionCode(transaction.getMandate().getBank().getNipBankCode());
        fTSingleDebitRequest.setMandateReferenceNumber(transaction.getMandate().getMandateCode());
        fTSingleDebitRequest.setNarration(transaction.getMandate().getSubscriberCode() + "_" + transaction.getMandate().getProduct().getBiller().getRcNumber()
				+ "_" + transaction.getMandate().getMandateCode());
        fTSingleDebitRequest.setPaymentReference(new StringBuilder(Constants.PAYMENT_REFERENCE_PREFIX).append(transaction.getMandate().getSubscriberCode()).toString());
        fTSingleDebitRequest.setSessionID(sessionId);
        
        log.trace("The  NIP debit fund transfer request for the mandate code {} with transaction ID {}: {}", transaction.getMandate().getMandateCode(), transaction.getId(), CommonUtils.convertObjectToXml(fTSingleDebitRequest));
        FTSingleDebitResponse response = nipService.doDirectDebit(fTSingleDebitRequest);
        log.trace("The NIP debit fund transfer response for the mandate code {} with transaction ID {}: {}", transaction.getMandate().getMandateCode(), transaction.getId(), CommonUtils.convertObjectToXml(response));
        return response;
    }
    
    private synchronized FTSingleCreditResponse creditBiller(Transaction transaction, BigDecimal amount, String sessionId) throws NipException {
        NESingleResponse nESingleResponse = nameEnquiry(transaction.getMandate(), Constants.DEBIT_ACTION);
        Mandate mandate = transaction.getMandate();
        if (null == nESingleResponse || !nESingleResponse.getResponseCode().trim().equals(Constants.SUCCESSFUL))
            return null;
        
        // TODO: Also remember to include the fee as agreed to align with the concept of ebillspay
        
        // build the fund transfer single credit request
        FTSingleCreditRequest fTSingleCreditRequest = new FTSingleCreditRequest();
        fTSingleCreditRequest.setBeneficiaryAccountName(mandate.getBiller().getAccountName());
        fTSingleCreditRequest.setBeneficiaryAccountNumber(mandate.getBiller().getAccountNumber());
        fTSingleCreditRequest.setBeneficiaryBankVerificationNumber(nESingleResponse.getBankVerificationNumber());
        fTSingleCreditRequest.setBeneficiaryKYCLevel(String.valueOf(nESingleResponse.getKYCLevel()));
        fTSingleCreditRequest.setChannelCode(nibssChannelCode);
        fTSingleCreditRequest.setDestinationInstitutionCode(mandate.getBiller().getBank().getNipBankCode());
        fTSingleCreditRequest.setNameEnquiryRef(nESingleResponse.getSessionID());
        fTSingleCreditRequest.setTransactionLocation("");
        fTSingleCreditRequest.setAmount(amount); // amount set successfully 
        fTSingleCreditRequest.setOriginatorAccountName(mandate.getAccountName());
        fTSingleCreditRequest.setOriginatorAccountNumber(mandate.getAccountNumber());
        fTSingleCreditRequest.setOriginatorBankVerificationNumber(mandate.getBvn());
        fTSingleCreditRequest.setOriginatorKYCLevel(String.valueOf(mandate.getKycLevel()));
        fTSingleCreditRequest.setNarration(mandate.getSubscriberCode() + "_" + mandate.getProduct().getBiller().getRcNumber()
				+ "_" + mandate.getMandateCode());
        fTSingleCreditRequest.setPaymentReference(new StringBuilder(Constants.PAYMENT_REFERENCE_PREFIX).append(mandate.getMandateCode()).toString());
        fTSingleCreditRequest.setSessionID(sessionId);
        
        log.trace("The  NIP credit fund transfer request for the mandate code {} with transaction ID {}: {}", mandate.getMandateCode(), transaction.getId(), CommonUtils.convertObjectToXml(fTSingleCreditRequest));
        FTSingleCreditResponse response = nipService.doCredit(fTSingleCreditRequest);
        log.trace("The  NIP credit fund transfer response for the mandate code {} with transaction ID {}: {}", mandate.getMandateCode(), transaction.getId(), CommonUtils.convertObjectToXml(response));
        return response;
    }
    
    /**
     * Gets the amount a customer will be debited
     * @param transaction
     * @return 
     */
    private BigDecimal getDebitAmount(Transaction transaction) {
        return transaction.getBearer() == FeeBearer.SUBSCRIBER ? transaction.getAmount().add(transaction.getFee()) : transaction.getAmount();
    }
    
    /**
     * Gets the amount a biller will be credited
     * @param transaction
     * @return 
     */
    private BigDecimal getCreditAmount(Transaction transaction) {
        if (transaction.getBearer() == FeeBearer.BILLER && transaction.isBillableAtTransactionTime())
            return transaction.getAmount().subtract(transaction.getFee());
        else if (transaction.getBearer() == FeeBearer.BILLER && !transaction.isBillableAtTransactionTime()) {
            return transaction.getAmount();
        } else if (transaction.getBearer() == FeeBearer.SUBSCRIBER && !transaction.isBillableAtTransactionTime()) {
            return transaction.getAmount().add(transaction.getFee());
        } else 
            return transaction.getAmount();
    }
}
