package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.dashboard.SyncActivityDashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stanlee on 15/05/2018.
 */
@RestController
public class SyncActivityController {
/*

    private EnrollmentReportService enrollmentReportService;
    private AgentMgrService agentMgrService;

    @Autowired
    public void setEnrollmentReportService(EnrollmentReportService enrollmentReportService) {
        this.enrollmentReportService = enrollmentReportService;
    }

    @Autowired
    public void setSubscriberService(AgentMgrService agentMgrService) {
        this.agentMgrService = agentMgrService;
    }

    @GetMapping("/sync-report/dashboard")
    public ResponseEntity syncReportOnDashboardByUserType(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                          @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date from,
                                                          @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date to){

        if (from == null){
            from = CommonUtils.startOfDay(new Date());
        }
        if (to == null){
            to = CommonUtils.endOfDay(new Date());
        }

        UserType userType = UserType.valueOf(userDetail.getUserType());
        switch (userType){

            case AGENT_MANAGER:
                return ResponseEntity.ok(new SyncActivityDashboard(enrollmentReportService.syncActivityReportsForAgentManager(from,to,true, userDetail.getInstitutionCode()),
                        enrollmentReportService.syncActivityReportsForAgentManager(from,to,false, userDetail.getInstitutionCode())));

            case NIBSS:
                return ResponseEntity.ok(new SyncActivityDashboard(enrollmentReportService.syncActivityReportsForNibss(from,to,true),
                        enrollmentReportService.syncActivityReportsForNibss(from,to,false)));
        }

        return ResponseEntity.ok(new SyncActivityDashboard("0","0"));

    }

    @GetMapping("/sync-report")
    public ResponseEntity getSynchReportByUserType(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                   @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date from,
                                                   @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date to,
                                                   @RequestParam boolean active,
                                                   @RequestParam(required = false, value = "param") String searchParam,
                                                   @RequestParam int pageNumber,
                                                   @RequestParam int pageSize){
        if (from == null){
            from = CommonUtils.startOfDay(new Date());
        }
        if (to == null){
            to = CommonUtils.endOfDay(new Date());
        }

        UserType userType = UserType.valueOf(userDetail.getUserType());
        switch (userType){

            case AGENT_MANAGER:
                return ResponseEntity.ok(enrollmentReportService.syncActivityReportsForAgentManager(from, to, active, searchParam, userDetail.getInstitutionCode(), new PageRequest(pageNumber,pageSize)));

            case NIBSS:
                return ResponseEntity.ok(enrollmentReportService.syncActivityReportsForNibss(from, to, active, searchParam, new PageRequest(pageNumber,pageSize)));
        }


        List<SyncActivityReport> activityReports = new ArrayList<>();
        return ResponseEntity.ok(activityReports);
    }

    @GetMapping("/sync-report/{agentManagerCode}")
    public ResponseEntity getSynchReportByAgentManagerCode(@DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date from,
                                                           @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date to,
                                                           @RequestParam boolean active,
                                                           @RequestParam(required = false, value = "param") String searchParam,
                                                           @RequestParam int pageNumber,
                                                           @RequestParam int pageSize,
                                                           @PathVariable("agentManagerCode") String agentManagerCode){
        if (from == null){
            from = CommonUtils.startOfDay(new Date());
        }
        if (to == null){
            to = CommonUtils.endOfDay(new Date());
        }

        AgentManager agentManager = agentMgrService.getByCode(agentManagerCode);
        if (agentManager == null){
            return ResponseEntity.badRequest().body(new ErrorDetails(Errors.INVALID_DATA_PROVIDED.getValue().replace("{}","agent manager code")));
        }

        return ResponseEntity.ok(enrollmentReportService.syncActivityReportsForAgentManager(from, to, active, searchParam, agentManager.getAgentManagerInstitution().getCode(), new PageRequest(pageNumber,pageSize)));
    }

    //get sync report in bulk
    @GetMapping(value = "/bulk/sync-report", produces = "text/csv")
    public void exportSyncReportInCSV(HttpServletResponse response,
                                                     @ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                     @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date from,
                                                     @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date to,
                                                     @RequestParam boolean active,
                                                     @RequestParam(required = false, value = "param") String searchParam) throws Exception{

        response.setHeader("Content-disposition", "attachment;filename=report.csv");
        if (from == null){
            from = CommonUtils.startOfDay(new Date());
        }
        if (to == null){
            to = CommonUtils.endOfDay(new Date());
        }

        UserType userType = UserType.valueOf(userDetail.getUserType());
        switch (userType){

            case AGENT_MANAGER:
                GenerateCSV.exportSyncActivityReport(response, enrollmentReportService.syncActivityReportsForAgentManagerAsList(from, to, active, searchParam, userDetail.getInstitutionCode()));
                break;

            case NIBSS:
                GenerateCSV.exportSyncActivityReport(response, enrollmentReportService.syncActivityReportsForNibssAsList(from, to, active, searchParam));
                break;

            default:
                List<SyncActivityReport> activityReports = new ArrayList<>();
                GenerateCSV.exportSyncActivityReport(response, activityReports);
                break;
        }

    }

    @GetMapping(value = "/bulk/sync-report/{agentManagerCode}", produces = "text/csv")
    public void exportSyncReportByAgentManagerCodeInCSV(HttpServletResponse response,
                                      @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date from,
                                      @DateTimeFormat(pattern = Format.date) @RequestParam(required = false) Date to,
                                      @RequestParam boolean active,
                                      @RequestParam(required = false, value = "param") String searchParam,
                                      @PathVariable("agentManagerCode") String agentManagerCode) throws Exception{

        response.setHeader("Content-disposition", "attachment;filename=report.csv");
        if (from == null){
            from = CommonUtils.startOfDay(new Date());
        }
        if (to == null){
            to = CommonUtils.endOfDay(new Date());
        }

        AgentManager agentManager = agentMgrService.getByCode(agentManagerCode);
        if (agentManager == null){
            List<SyncActivityReport> activityReports = new ArrayList<>();
            GenerateCSV.exportSyncActivityReport(response, activityReports);
        }

        GenerateCSV.exportSyncActivityReport(response, enrollmentReportService.syncActivityReportsForAgentManagerAsList(from, to, active, searchParam, agentManager.getAgentManagerInstitution().getCode()));

    }
*/

}
