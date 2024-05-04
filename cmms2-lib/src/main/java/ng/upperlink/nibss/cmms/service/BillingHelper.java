/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.model.Transaction;
import org.springframework.core.io.Resource;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
public interface BillingHelper {
    static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    static BigDecimal ONE_HUNDRED = new BigDecimal(100);
    static  String HEADERS[] = {
            "TRANSACTION ID",
            "FEE BEARER",
            "MARK UP FEE BANK",
            "MARK UP FEE ACCOUNT NUMBER",
            "MARK UP FEE ACCOUNT NAME",
            "DEFAULT FEE",
            "MARK UP FEE",
            "AMOUNT",
            "MANDATE CODE",
            "TRANSACTION DATE"
    };
    
    ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        public DateFormat get() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        @Override
        public DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    /**
     * Process the NIBSSPay File
     * @param transactions
     * @return the payment content
     */
    String getNIBSSPayPaymentFile(List<NIBSSPayPayment> transactions);
    /**
     * 
     * @param transactions
     * @return the path for the transaction file
     */
    String getTransactionsFile(List<? extends Transaction> transactions);
    /**
     * Create Billing Path
     */
    void createBillingFolder(String cmmsBillingPath);
    /**
     * Generates the zip payment file
     * @param fileMap
     * @param zipFileLocation
     * @throws IOException 
     */
    void writeZipPaymentFile(Map<String, String> fileMap, Path zipFileLocation) throws IOException;
    
    /**
     * Process the smart det file
     * @param transactions
     * @return the smart DET file content
     */
    String getSmartDetFile(Map<String, BigDecimal> transactions);
    
    /**
     * Download a generated billing file
     * @param billingPath
     * @return the resource for the selected billing file
     */
    Resource downloadBillingReport(Path billingPath);
    
    /**
     * Clean files older that two days
     */
    void cleanUpOldFiles();
    
    /**
     * Convert amount to kobo
     * 
     * @param amount
     * @return the string version of the big decimal
     */
    default String getAmountInKobo(BigDecimal amount) {
        return amount.multiply(ONE_HUNDRED).setScale(0, ROUNDING_MODE).toPlainString();
    }
}
