package ng.upperlink.nibss.cmms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by stanlee on 18/07/2018.
 */
@RestController
@RequestMapping("/transactionreport")
public class TransactionReportController {

   /* private AgentTransactionReportService agentTransactionReportService;

    @Autowired
    public void setAgentTransactionReportService(AgentTransactionReportService agentTransactionReportService) {
        this.agentTransactionReportService = agentTransactionReportService;
    }

    //get the dashboard
        //As NIBSS - total volume and Value for all agent manager institution
        //As AgentManager - total volume and value for his/her agent manager institution
    @GetMapping("/dashboard")
    public ResponseEntity getDashboardReport(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail){
        return ResponseEntity.ok(agentTransactionReportService.getDashboard(userDetail));
    }

    //get report breakdown
        //As NIBSS - report displayed grouped by agent manager institution which is searchable, paginated and exportable
            //when an agent manager is clicked - report displayed is grouped by agent which is searchable, paginated and exportable
        //As Agent manager - report displayed group by agent which is searchable, paginated and exportable

    //get report by transaction type breakdown
        //As NIBSS - report displayed grouped by agent manager institution which is searchable, paginated and exportable
            //when an agent manager is clicked - report displayed is grouped by agent which is searchable, paginated and exportable
        //As Agent manager - report displayed group by agent which is searchable, paginated and exportable

    @GetMapping
    public ResponseEntity getReport(PageSearchWithDate pageSearchWithDate,
                                         @RequestParam(required = false) String serviceProvided,
                                         @RequestParam(required = false) String institutionCode){

        if (institutionCode == null) {
            if (pageSearchWithDate.isPageable()) {

                if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
                    return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByInstitution(pageSearchWithDate));
                }
                return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByInstitutionAndTransactionType(ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));
            }

            if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
                return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByInstitutionAsList(pageSearchWithDate));
            }
            return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByInstitutionAndTransactionTypeAsList(ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));
        }

        if (pageSearchWithDate.isPageable()) {

            if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
                return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByAgent(institutionCode, pageSearchWithDate));
            }
            return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByAgentAndTransactionType(institutionCode, ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));
        }

        if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
            return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByAgentAsList(institutionCode, pageSearchWithDate));
        }
        return ResponseEntity.ok(agentTransactionReportService.getTabularReportGroupByAgentAndTransactionTypeAsList(institutionCode, ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));

    }

    //get transactions in bulk.
    @GetMapping(value = "/bulk", produces = "text/csv")
    public void exportReportInCSV(HttpServletResponse response,
                                  PageSearchWithDate pageSearchWithDate,
                                  @RequestParam(required = false) String serviceProvided,
                                  @RequestParam(required = false) String institutionCode) throws Exception{

        response.setHeader("Content-disposition", "attachment;filename=report.csv");

        if (institutionCode == null) {

            if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
                GenerateCSV.exportTabularReport(response, agentTransactionReportService.getTabularReportGroupByInstitutionAsList(pageSearchWithDate));
            } else {
                GenerateCSV.exportTabularReportByType(response, agentTransactionReportService.getTabularReportGroupByInstitutionAndTransactionTypeAsList(ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));
            }
        } else {

            if (serviceProvided == null || ServicesProvided.findByValue(serviceProvided) == ServicesProvided.UNKNOWN) {
                GenerateCSV.exportTabularReport(response, agentTransactionReportService.getTabularReportGroupByAgentAsList(institutionCode, pageSearchWithDate));
            } else {
                GenerateCSV.exportTabularReportByType(response, agentTransactionReportService.getTabularReportGroupByAgentAndTransactionTypeAsList(institutionCode, ServicesProvided.findByValue(serviceProvided), pageSearchWithDate));
            }
        }
    }

    @GetMapping("/bystateandlga")
    public ResponseEntity getReportGroupByStateAndLga(PageSearchWithDate pageSearchWithDate){

        if (pageSearchWithDate.isPageable()) {
            return ResponseEntity.ok(agentTransactionReportService.getReportByStateAndLga(pageSearchWithDate));
        }

        return ResponseEntity.ok(agentTransactionReportService.getReportByStateAndLgaAsList(pageSearchWithDate));
    }

    //get transactions group by state and lga in bulk.
    @GetMapping(value = "/bystateandlga/bulk", produces = "text/csv")
    public void exportReportGroupByStateAndLgaInCSV(HttpServletResponse response, PageSearchWithDate pageSearchWithDate) throws Exception{

        response.setHeader("Content-disposition", "attachment;filename=report.csv");
        GenerateCSV.exportTransactionCountByStateAndLga(response, agentTransactionReportService.getReportByStateAndLgaAsList(pageSearchWithDate));

    }
*/
}
