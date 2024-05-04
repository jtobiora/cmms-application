package ng.upperlink.nibss.cmms.export;

import ng.upperlink.nibss.cmms.dto.transactionReport.*;
import ng.upperlink.nibss.cmms.embeddables.TransactionAmountPerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenerateCSV {

    private static final Logger logger = LoggerFactory.getLogger(GenerateCSV.class);

   /* public static void exportReport(HttpServletResponse response, List<TransactionReport> transactionReports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("TRANSACTION DATE, AMOUNT, AGENT CODE, CODE, CREATED AT");
        rows.add("\n");

       *//* for (TransactionReport transactionReport : transactionReports) {
            TransactionAmountPerChannel[] transactionAmountPerChannels = transactionReport.getTransactionAmountPerChannels().toArray(new TransactionAmountPerChannel[transactionReport.getTransactionAmountPerChannels().size()]);
            rows.add(transactionReport.getTransactionDate()+","+
                    ((transactionAmountPerChannels.length > 0 )? transactionAmountPerChannels[0].getAmount() : BigDecimal.ZERO)+","+
                    transactionReport.getAgentCode()+","+
                    transactionReport.getCode()+","+
                    transactionReport.getCreatedAt());
            rows.add("\n");
        } *//*

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }*/

   /* public static void exportSuccessBVNReport(HttpServletResponse response, List<SuccessBVNReport> reports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("WEEKEND DATE, BVN, AMOUNT, NARRATION");
        rows.add("\n");

      *//*  for (SuccessBVNReport report : reports) {

            rows.add(report.getDate()+","+
                    report.getBvn()+","+
                    report.getAmount()+","+
                    report.getNarration());
            rows.add("\n");
        }*//*

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }*/

  /*  public static void exportAgentManagerBVMPaymentReport(HttpServletResponse response, List<AgentManagerPaymentReport> reports, BigDecimal feePerEnrollment) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, AGENT MANAGER CODE, ACCOUNT NUMBER, BANK CODE, AMOUNT, ACCOUNT NAME, NARRATION");
        rows.add("\n");

        int counter = 0;
        for (AgentManagerPaymentReport report : reports) {

            rows.add( ++counter+","+
                    report.getAgentManagerCode()+","+
                    report.getAccountNumber()+","+
                    report.getBankCode()+","+
                    BigDecimal.valueOf(report.getCountOfBvn()).multiply(feePerEnrollment)+","+
                    report.getAccountName()+","+
                    "Payment report ");
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }*/

    //Get Transaction Report in CSV
    public static void exportTabularReport(HttpServletResponse response, List<TabularReport> reports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, NAME, CODE, TOTALVOLUME, TOTALVALUE,  " +
                "ACCOUNTOPENINGVOLUME, ACCOUNTOPENINGVALUE, " +
                "ADDITIONALSERVICE1VOLUME, ADDITIONALSERVICE1VALUE, " +
                "ADDITIONALSERVICE2VOLUME, ADDITIONALSERVICE2VALUE, " +
                "AIRTIMERECHARGEVOLUME, AIRTIMERECHARGEVALUE, " +
                "BILLPAYMENTVOLUME, BILLPAYMENTVALUE, " +
                "BVNENROLLMENTVOLUME, BVNENROLLMENTVALUE, " +
                "CASHINVOLUME, CASHINVALUE, " +
                "CASHOUTVOLUME, CASHOUTVALUE, " +
                "FUNDTRANSFERVOLUME, FUNDTRANSFERVALUE, " +
                "OTHERSVOLUME, OTHERSVALUE");
        rows.add("\n");

        int counter = 0;
        for (TabularReport report : reports) {

            rows.add( ++counter+","+
                    report.getName()+","+
                    report.getCode()+","+
                    report.getTotalVolume()+","+
                    report.getTotalValue()+","+
                    report.getAccountOpeningVolume()+","+
                    report.getAccountOpeningValue()+","+
                    report.getAdditionalService1Volume()+","+
                    report.getAdditionalService1Value()+","+
                    report.getAdditionalService2Volume()+","+
                    report.getAdditionalService2Value()+","+
                    report.getAirtimeRechargeVolume()+","+
                    report.getAirtimeRechargeValue()+","+
                    report.getBillPaymentVolume()+","+
                    report.getBillPaymentValue()+","+
                    report.getBvnEnrollmentVolume()+","+
                    report.getBvnEnrollmentValue()+","+
                    report.getCashInVolume()+","+
                    report.getCashInValue()+","+
                    report.getCashOutVolume()+","+
                    report.getCashOutValue()+","+
                    report.getFundTransferVolume()+","+
                    report.getFundTransferValue()+","+
                    report.getOthersVolume()+","+
                    report.getOthersValue());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }

    //Get Transaction Report By Type in CSV
    public static void exportTabularReportByType(HttpServletResponse response, List<TabularReportByReportType> reports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, NAME, CODE, VOLUME, VALUE");
        rows.add("\n");

        int counter = 0;
        for (TabularReportByReportType report : reports) {

            rows.add( ++counter+","+
                    report.getName()+","+
                    report.getCode()+","+
                    report.getTotalVolume()+","+
                    report.getTotalValue());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }

    //Get Count By State in CSV

    /**
     * Get Count By State in CSV
     * @param response
     * @param reports
     * @param nameOfCount Agent, Agent manager institution or Agent manager user etc
     * @throws Exception
     */
    public static void exportAgentCountByState(HttpServletResponse response, List<CountByStateReport> reports, String nameOfCount) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, STATE NAME, "+(nameOfCount == null ? "COUNT" : "COUNT OF "+nameOfCount.toUpperCase()));
        rows.add("\n");

        int counter = 0;
        for (CountByStateReport report : reports) {

            rows.add( ++counter+","+
                    report.getStateName()+","+
                    report.getCount());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }

    //Get Count By State and Lga in CSV

    /**
     * Get Count By State and Lga in CSV
     * @param response
     * @param reports
     * @param nameOfCount
     * @throws Exception
     */
    public static void exportAgentCountByStateAndLga(HttpServletResponse response, List<CountByStateAndLgaReport> reports, String nameOfCount) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, STATE NAME, LGA NAME, "+(nameOfCount == null ? "COUNT" : "COUNT OF "+nameOfCount.toUpperCase()));
        rows.add("\n");

        int counter = 0;
        for (CountByStateAndLgaReport report : reports) {

            rows.add( ++counter+","+
                    report.getStateName()+","+
                    report.getLgaName()+","+
                    report.getCount());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }

    //Get Transaction Count (volume and value) By State and Lga in CSV
    public static void exportTransactionCountByStateAndLga(HttpServletResponse response, List<TransactionReportByStateAndLga> reports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, STATE NAME, LGA NAME, COUNT OF VOLUME, COUNT OF VALUE");
        rows.add("\n");

        int counter = 0;
        for (TransactionReportByStateAndLga report : reports) {

            rows.add( ++counter+","+
                    report.getStateName()+","+
                    report.getLgaName()+","+
                    report.getTotalVolume()+","+
                    report.getTotalValue());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }

    //Get Count Agents group by institution in CSV
   /* public static void exportAgentCountByInstitution(HttpServletResponse response, List<AgentCountByInstitution> reports) throws Exception {

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, INSTITUTION NAME, COUNT OF AGENT");
        rows.add("\n");

        int counter = 0;
        for (AgentCountByInstitution report : reports) {

            rows.add( ++counter+","+
                    report.getInstitutionName()+","+
                    report.getCountOfAgent());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();
    }*/

    //Get Count Agents group by institution in CSV
   /* public static void exportSyncActivityReport(HttpServletResponse response, List<SyncActivityReport> reports) throws Exception {

      *//*  ArrayList<String> rows = new ArrayList<String>();
        rows.add("SERIAL NUMBER, CODE, EMAIL ADDRESS, STATE NAME, LGA NAME, PHONE NUMBER, COUNT OF SYNC");
        rows.add("\n");

        int counter = 0;
        for (SyncActivityReport report : reports) {

            rows.add( ++counter+","+
                    report.getCode()+","+
                    report.getEmailAddress()+","+
                    report.getStateName()+","+
                    report.getLgaName()+","+
                    report.getPhoneNumber()+","+
                    report.getCountOfSync());
            rows.add("\n");
        }

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }

        response.getOutputStream().flush();*//*
    }*/
}
