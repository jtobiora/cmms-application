package ng.upperlink.nibss.cmms.controller;

import ng.upperlink.nibss.cmms.api.ReportSearch;
import ng.upperlink.nibss.cmms.api.ReportSearchWithParam;
import ng.upperlink.nibss.cmms.dashboard.DashboardDto;
import ng.upperlink.nibss.cmms.dashboard.DashboardProvider;
import ng.upperlink.nibss.cmms.dto.*;
import ng.upperlink.nibss.cmms.enums.Constants;
import ng.upperlink.nibss.cmms.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";

    private final DashboardProvider dashboardProvider;


    public DashboardController(DashboardProvider dashboardProvider) {

        this.dashboardProvider = dashboardProvider;
    }

    /*@GetMapping
    public DashboardDto getForUser(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail) {
        return dashboardProvider.getDashboardForUser(userDetail);
    }
*/

   /* @GetMapping("/statistics/state")
    public ResponseEntity<List<EnrollmentStatistic>> getStaticForState(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL)
                                                                               UserDetail userDetail,
                                                                       @RequestParam("state") String state) {

        UserType userType = UserType.find(userDetail.getUserType());

        switch (userType) {
            case NIBSS:
            case AGENT_MANAGER:
                String stateValue = getCompleteStateParam(state);
                return ResponseEntity.ok(enrollmentReportService.getEnrollmentStaticsForState(stateValue, userDetail));
                default:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }*/

    private String getCompleteStateParam(@RequestParam("state") String state) {
        return state.toLowerCase().endsWith("state") ? state :
                state + " State";
    }

/*
    @GetMapping("/agentmanagers")
    public Page<AgentManagerDto> agentManagers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                               ReportSearch search) {
        return dashboardProvider.getAgentManagers(userDetail, search);
    }

    @GetMapping("/agents")
    public Page<AgentDto> agents(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                 ReportSearch search) {
        return dashboardProvider.getAgents(userDetail, search);
    }

    @GetMapping("/enrollers")
    public Page<EnrollerDto> enrollers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                       ReportSearch search) {
        return dashboardProvider.getEnrollersForUser(userDetail, search);
    }


    @GetMapping("/branches")
    public Page<BranchDto> branches(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                    ReportSearch search) {
        return dashboardProvider.getBranchesForUser(userDetail, search);
    }


    @GetMapping("/enrollments")
    public Page<EnrollmentReportDto> enrollments(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                 ReportSearchWithParam search) {
        return dashboardProvider.getEnrollmentsForUser(userDetail, search);
    }

    @GetMapping("/enrollments-by-institutions")
    public Page<EnrollmentReportDto> enrollmentsByInstitution(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                 ReportSearchWithParam search) {
        return dashboardProvider.getEnrollmentsForUser(userDetail, search);
    }


    @GetMapping("/branches/download")
    public ResponseEntity<byte[]> downloadBranches
            (@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail, ReportSearch search) {

        return dashboardProvider.downloadBranches(userDetail, search)
                .map( outputStream -> getDownload(search,outputStream, buildFileName("branches",search) ))
                .orElseGet( () -> ResponseEntity.noContent().build());

    }


    @GetMapping("/enrollers/download")
    public ResponseEntity<byte[]> downloadEnrollers
            (@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail, ReportSearch search) {

        return dashboardProvider.downloadEnrollers(userDetail, search)
                .map(outputStream -> getDownload(search, outputStream, buildFileName("enrollers", search)))
                .orElseGet(() -> ResponseEntity.noContent().build());

    }


    @GetMapping("/enrollments/download")
    public ResponseEntity<byte[]> downloadEnrollmentReports
            (@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail, ReportSearch search) {

        return dashboardProvider.downloadEnrollmentReports(userDetail, search)
                .map( outputStream -> getDownload(search,outputStream, buildFileName("enrollmentReports",search) ))
                .orElseGet( () -> ResponseEntity.noContent().build());
    }


    @GetMapping("/agents/download")
    public ResponseEntity<byte[]> downloadAgents (@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                  ReportSearch search) {
        return dashboardProvider.downloadAgents(userDetail, search)
                .map( outputStream ->  getDownload(search, outputStream, buildFileName("agents", search)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/agentmanagers/download")
    public ResponseEntity<byte[]> downloadAgentManagers(@ApiIgnore @RequestAttribute(Constants.USER_DETAIL) UserDetail userDetail,
                                                        ReportSearch search) {

        return dashboardProvider.downloadAgentManagers(userDetail, search)
                .map( outputStream ->  getDownload(search, outputStream, buildFileName("agents", search)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }*/



    private ResponseEntity<byte[]> getDownload(ReportSearch search, ByteArrayOutputStream outputStream, String fileName) {
        return ResponseEntity
                .ok().header("Content-Disposition", getContentDisposition(fileName))
                .header("Content-Type", search.getDownloadType().getMimeType())
                .contentLength(outputStream.size())
                .body(outputStream.toByteArray());
    }


    private String getContentDisposition(String fileName) {
        return String.format("attachment; filename=%s;", fileName);
    }

    private String buildFileName(String baseName, ReportSearch search) {
        DateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
        return String.format("%s_report_%s_to_%s.%s",
                baseName,
                fmt.format(search.getFrom()),
                fmt.format(search.getTo()),
                search.getDownloadType().getExtension()
        );
    }




}
