/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import com.nibss.nip.NipException;
import com.nibss.nip.NipService;
import com.nibss.nip.crypto.FileSSMCipher;
import com.nibss.nip.dto.FTSingleCreditRequest;
import com.nibss.nip.dto.FTSingleCreditResponse;
import com.nibss.nip.dto.FTSingleDebitRequest;
import com.nibss.nip.dto.FTSingleDebitResponse;
import com.nibss.nip.dto.MandateAdviceRequest;
import com.nibss.nip.dto.MandateAdviceResponse;
import com.nibss.nip.dto.NESingleRequest;
import com.nibss.nip.dto.NESingleResponse;
import com.nibss.nip.impl.JaxwsNipService;
import java.net.MalformedURLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class CmmsNipService {
    
    @Value("${nip.url}")
    private String nipUrl;
    
    @Value("${requesting.institution}")
    private String requestingInstitution;
    
    @Value("${cipher.key.dir}")
    private String cipherKeyDirectory;
    
    @Value("${cipher.password}")
    private String cipherPassword;
    
    @Value("${cipher.bank.code}")
    private String cipherBankCode;
    
    private NipService createNipService() throws MalformedURLException {
        return new JaxwsNipService(nipUrl, new FileSSMCipher(requestingInstitution, cipherKeyDirectory, cipherPassword, cipherBankCode));
    }
    
    /**
     * Name Equiry Method
     * @param nameEnquiryRequest
     * @return
     * @throws NipException 
     */
    public NESingleResponse doNameEnquiry(NESingleRequest nameEnquiryRequest) throws NipException {
        NESingleResponse response = null;
        try {
            response = createNipService().doNameEnquiry(nameEnquiryRequest);
            log.trace("The response code for name enquiry for the account is: {}", response.getResponseCode());
        } catch (MalformedURLException ex) {
            log.error("An exception occurred while executing the name enquiry operation for the request ==> {}", nameEnquiryRequest, ex);
            throw new NipException(ex);
        }
        return response;
    }
    
    /**
     * Manate Advice Method
     * @param mandateAdviceRequest
     * @return
     * @throws NipException 
     */
    public MandateAdviceResponse doMandateAdvise(MandateAdviceRequest mandateAdviceRequest) throws NipException {
        MandateAdviceResponse response = null;
        try {
            response = createNipService().doMandateAdvice(mandateAdviceRequest);
            log.trace("The response for mandate advise for the request {} is ===> {}", mandateAdviceRequest, response);
        } catch (MalformedURLException ex) {
            log.error("An exception occured while carrying out the mandate advise operation for the request ===> {}", mandateAdviceRequest, ex);
            throw new NipException(ex);
        }
        return response;
    }
    
    /**
     * Direct Debit Operations
     * @param fTSingleDebitRequest
     * @return
     * @throws NipException 
     */
    public FTSingleDebitResponse doDirectDebit(FTSingleDebitRequest fTSingleDebitRequest) throws NipException {
        FTSingleDebitResponse response = null;
        try {
            response = createNipService().doFTSingleDebit(fTSingleDebitRequest);
            log.trace("The response from NIP for the debit operation is ==> {} and the request is => {}", response, fTSingleDebitRequest);
        } catch (MalformedURLException ex) {
            log.error("An exception occurred while carrying out the debit for the selected customer with request {}", fTSingleDebitRequest, ex);
            throw new NipException(ex);
        }
        return response;
    }
    
    public FTSingleCreditResponse doCredit(FTSingleCreditRequest fTSingleCreditRequest) throws NipException {
        FTSingleCreditResponse response = null;
        try {
            response = createNipService().doFTSingleCredit(fTSingleCreditRequest);
            log.trace("The response from NIP after credit is ==> {} and the request is {}", response, fTSingleCreditRequest);
        } catch (MalformedURLException ex) {
            log.error("An exception occurred while credit the account with request {}", fTSingleCreditRequest, ex);
            throw new NipException(ex);
        }
        return response;
    }
}
