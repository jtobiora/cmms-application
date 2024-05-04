/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.upperlink.nibss.cmms.service;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.dto.NIBSSPayPayment;
import ng.upperlink.nibss.cmms.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.LongAdder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Megafu Charles <noniboycharsy@gmail.com>
 */
@Service
@Slf4j
public class BillingHelperService implements BillingHelper {
    
    @Value("${cmms.bank.suffix}")
    private String sortCodeSuffix;

    @Value("${cmms.smartdet.code}")
    private String smartDetCode;

    @Value("${cmms.narration}")
    private String cmmsNarration;

    @Value("${cmms.payer.name}")
    private String payerName;
    
    @Value("${billing.payment.temporary.folder}")
    private String cmmsTempBillingFolder;
    
    private LongAdder adder;

    /** {@inheritDoc} */
    @Override
    public synchronized String getNIBSSPayPaymentFile(List<NIBSSPayPayment> transactions) {
        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT;
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            adder = new LongAdder();
            adder.increment();
            transactions.stream().forEach((a) -> {
                List<String> items = new ArrayList<>();
                items.add(String.valueOf(adder.intValue()));
                items.add(a.getAccount().getAccountNumber());
                items.add(String.format("%s%s", a.getAccount().getBankCode(), sortCodeSuffix));
                items.add(this.getAmountInKobo(a.getAmount()));
                items.add(a.getAccount().getAccountName());
                items.add(cmmsNarration);
                items.add(payerName);
                try {
                    printer.printRecord(items);
                } catch (IOException ex) {
                    log.error("Could not add NIBSS Pay record to printer", ex);
                }
                adder.increment();
            });
            return writer.toString();
        } catch(IOException e) {
            throw new RuntimeException("Could not generate NIBSSPAY payment file", e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getSmartDetFile(Map<String, BigDecimal> transactions) {
        String dateColumn = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date());
        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT;
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            transactions.keySet().stream().forEach((b) -> {
                List<String> items = new ArrayList<>();
                items.add(String.format("%s%s", b, sortCodeSuffix));
                items.add(smartDetCode);
                items.add(dateColumn);
                items.add(transactions.get(b).setScale(2, ROUNDING_MODE).toPlainString());
                try {
                    printer.printRecord(items);
                } catch (IOException ex) {
                    log.error("Unable to print records to buffer", ex);
                }
            });

            return writer.toString();
        } catch(IOException e) {
            throw new RuntimeException("Could not generate smartdet file", e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void createBillingFolder(String cmmsBillingPath) {
        Path path = Paths.get(cmmsBillingPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException ex) {
                log.error("Could not create CMMS billing path", ex);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void writeZipPaymentFile(Map<String, String> fileMap, Path zipFileLocation) throws IOException {
        try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFileLocation, StandardOpenOption.CREATE)))) {
            fileMap.keySet().stream().forEach((f) -> {
                try {
                    String content = fileMap.get(f);
                    ZipEntry entry = new ZipEntry(f);
                    outputStream.putNextEntry(entry);
                    outputStream.write(content.getBytes());
                    outputStream.closeEntry();
                } catch (IOException e) {
                    log.error("Unable to add file entry to the zip file: {}", f, e);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getTransactionsFile(List<? extends Transaction> transactions) {
        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.EXCEL;
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            printer.printRecord(this.HEADERS);
            transactions.stream().forEach((t) -> {
                List<String> items = new ArrayList<>();
                items.add(t.getSuccessfulSessionId());
                items.add(t.getBearer().getValue());
                items.add(t.getBank().getCode());
                items.add(t.getAccountNumber());
                items.add(t.getAccountName());
                items.add(t.getDefaultFee().toPlainString());
                items.add(t.isMarkedUp() ? t.getFee().subtract(t.getDefaultFee()).toPlainString() : "0");
                items.add(t.getAmount().toPlainString());
                items.add(t.getMandate().getMandateCode());
                items.add(DATE_FORMAT_THREAD_LOCAL.get().format(t.getPaymentDate()));
                try {
                    printer.printRecord(items);
                } catch (IOException e) {
                    log.error("Could not write transaction detail to printer: {}", t.getSuccessfulSessionId(), e);
                }
            });
            return writer.toString();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Resource downloadBillingReport(Path billingPath) {
        try {
            Resource resource = new UrlResource(billingPath.toUri());
            if (resource.exists() || resource.isReadable()) 
                return resource;
            else 
                throw new RuntimeException("Could not read resource file: " + billingPath);
        }
        catch (MalformedURLException e) {
            log.error("Could not read resource file: {}", billingPath, e);
            throw new RuntimeException("Could not read resource file: " + billingPath);
        }
    }

    /** {@inheritDoc} */
    @Async
    @Override
    public void cleanUpOldFiles() {
        try {
            Files.list(Paths.get(cmmsTempBillingFolder)).forEach((Path f) -> {
                LocalDate dateLastModified = new Date(f.toFile().lastModified()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = LocalDate.now();
                long difference = DAYS.between(dateLastModified, now);
                
                if (difference >= 2) { // delete files older than or equal two days
                    try {
                         Files.delete(f);
                    } catch (IOException e) {
                        log.error("Could not delete the file: {}", f, e);
                    }
                }
            });
        } catch (IOException e) {
            log.error("Could not read temporary folder: {}", cmmsTempBillingFolder, e);
        }
    }
    
}
