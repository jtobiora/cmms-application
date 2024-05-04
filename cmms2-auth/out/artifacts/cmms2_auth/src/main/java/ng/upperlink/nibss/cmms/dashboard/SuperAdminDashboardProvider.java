package ng.upperlink.nibss.cmms.dashboard;


import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Slf4j
class SuperAdminDashboardProvider {
/*
    private BranchService branchService;
    private AgentMgrService agentMgrService;
    private EnrollerService enrollerService;
    private AgentService agentService;
    private UserService userService;
    private EnrollmentReportService enrollmentReportService;
    private AgentManagerInstitutionService agentManagerInstitutionService;

    private final ReportGenerator reportGenerator;


    SuperAdminDashboardProvider(BranchService branchService,
                                AgentMgrService agentMgrService,
                                EnrollerService enrollerService, AgentService agentService,
                                UserService userService, EnrollmentReportService enrollmentReportService,
                                ReportGenerator reportGenerator, AgentManagerInstitutionService agentManagerInstitutionService) {

        this.branchService = branchService;
        this.agentMgrService = agentMgrService;
        this.enrollerService = enrollerService;
        this.agentService = agentService;
        this.userService = userService;

        this.enrollmentReportService = enrollmentReportService;
        this.agentManagerInstitutionService = agentManagerInstitutionService;

        this.reportGenerator = reportGenerator;
    }

    SuperAdminDashboardDto getDashboard() {
        SuperAdminDashboardDto dto = new SuperAdminDashboardDto();

        try {
            dto.setInstitutionCount(agentManagerInstitutionService.count());
            dto.setAgentCount(agentService.count());
            dto.setAgentManagerCount(agentMgrService.count());
            dto.setBranchCount(branchService.count());
            dto.setEnrollerCount(enrollerService.count());

            dto.setNibssUserCount(userService.countByType(UserType.NIBSS));

            dto.setFailedEnrollmentCount(enrollmentReportService.countAllFailedEnrollments());
            dto.setSuccessfulEnrollmentCount(enrollmentReportService.countAllSuccessfulEnrollments());

            dto.setPendingEnrollmentCount(enrollmentReportService.countAllPendingEnrollments());


        } catch (RuntimeException e) {
            log.error("error occurred while getting super admin dashboard", e);
        }

        return dto;
    }

    Page<BranchDto> getBranchesForNibss(Pageable pageable, ReportSearch search) {
        if (search.isDateEmpty()) {
            return branchService.findAllStrippedDown(pageable);
        } else {
            return branchService.findByDateRange(search.getFrom(), search.getTo(), pageable);
        }
    }


    Page<EnrollerDto> getEnrollersForNibss(Pageable pageable, ReportSearch reportSearch) {
        if (reportSearch.isDateEmpty()) {
            return enrollerService.findAllStrippedDown(pageable);
        } else {
            return enrollerService.findByDateRange(reportSearch.getFrom(),
                    reportSearch.getTo(), pageable);
        }
    }

    Page<EnrollmentReportDto> getEnrollmentReports(Pageable pageable, ReportSearchWithParam reportSearch) {
        if (reportSearch.isDateEmpty()) {
            return enrollmentReportService.findAllStrippedDown(reportSearch.getParam(), pageable);
        } else {
            return enrollmentReportService.findByDateRange(reportSearch.getFrom(), reportSearch.getTo(), reportSearch.getParam(), pageable);
        }
    }

    ByteArrayOutputStream downloadBranches(ReportSearch reportSearch) {

        List<BranchDto> branches = branchService.findByDateRange(reportSearch.getFrom(),
                reportSearch.getTo());

        if (branches == null || branches.isEmpty())
            return null;

        TextColumnBuilder[] columns = {
                reportGenerator.getBranchStringColumn(BranchDto.Report.BRANCH_NAME),
                reportGenerator.getBranchStringColumn(BranchDto.Report.CITY),
                reportGenerator.getBranchStringColumn(BranchDto.Report.STATE),
                reportGenerator.getBranchStringColumn(BranchDto.Report.AGENT_CODE),
                reportGenerator.getBranchStringColumn(BranchDto.Report.AGENT_MANAGER_CODE),
                reportGenerator.getBranchDateColumn(BranchDto.Report.DATE_CREATED)
        };

        return reportGenerator.generateBranchReport(branches, reportSearch.getDownloadType(),
                columns);
    }

    ByteArrayOutputStream downloadEnrollers(ReportSearch reportSearch) {

        List<EnrollerDto> enrollers = enrollerService.findByDateRange(reportSearch.getFrom(),
                reportSearch.getTo());

        if (null == enrollers || enrollers.isEmpty())
            return null;

        TextColumnBuilder[] columns = {
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.FIRST_NAME),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LAST_NAME),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.EMAIL),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.BVN),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.EMAIL),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.ENROLLER_CODE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.AGENT_CODE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.AGENT_MANAGER_CODE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LGA),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.STATE),
                reportGenerator.getEnrollerDateColumn(EnrollerDto.Report.DATE_CREATED)
        };

        return reportGenerator.generateEnrollerReport(enrollers, reportSearch.getDownloadType(),
                columns);
    }

    ByteArrayOutputStream downloadEnrollmentReport(ReportSearch reportSearch) {

        List<EnrollmentReportDto> enrollmentReports = enrollmentReportService
                .findByDateRange(reportSearch.getFrom(), reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.TICKET_NUMBER),
                reportGenerator.getEnrollmentReportDateColumn(EnrollmentReportDto.Report.CAPTURE_DATE),
                reportGenerator.getEnrollmentReportDateColumn(EnrollmentReportDto.Report.SYNC_DATE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.LATITUDE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.LONGITUDE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.CITY),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.REGION),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.COUNTRY),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.AGENT_CODE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.ENROLLER_CODE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.VALIDATION_STATUS),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.FINGERPRINT_SCANNER),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.VALIDATION_MESSAGE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.BVN),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.BMS_TICKET_ID),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.RESPONSE_CODE),
                reportGenerator.getEnrollmentReportAmountColumn(EnrollmentReportDto.Report.AMOUNT)
        };

        return reportGenerator.generateEnrollmentReport(enrollmentReports, reportSearch.getDownloadType(),
                columns);
    }


    Page<AgentManagerDto> getAgentManagers(ReportSearch search, Pageable pageable) {

        if (search.isDateEmpty()) {
            return agentMgrService.findAllStrippedDown(pageable);
        } else {
            return agentMgrService.findByDateRange(search.getFrom(), search.getTo(), pageable);
        }
    }

    Page<AgentDto> getAgents(ReportSearch search, Pageable pageable) {
        if (search.isDateEmpty()) {
            return agentService.findAllStrippedDown(pageable);
        } else {
            return agentService.findByDateRange(search.getFrom(), search.getTo(), pageable);
        }
    }

    ByteArrayOutputStream downloadAgents(ReportSearch search) {


        List<AgentDto> agents = agentService.findByDateRangeExtended(search.getFrom(), search.getTo());
        TextColumnBuilder[] columns = {
                reportGenerator.getAgentStringColumn(AgentDto.Report.AGENT_CODE),
                reportGenerator.getAgentStringColumn(AgentDto.Report.AGENT_NAME),
                reportGenerator.getAgentStringColumn(AgentDto.Report.BVN),
                reportGenerator.getAgentStringColumn(AgentDto.Report.AGENT_MANAGER_INST_CODE),
                reportGenerator.getAgentStringColumn(AgentDto.Report.AGENT_MANAGER_INST_NAME),
                reportGenerator.getAgentStringColumn(AgentDto.Report.EMAIL),
                reportGenerator.getAgentStringColumn(AgentDto.Report.PHONE_NUMBER),
                reportGenerator.getAgentStringColumn(AgentDto.Report.ADDRESS),
                reportGenerator.getAgentStringColumn(AgentDto.Report.WARD),
                reportGenerator.getAgentStringColumn(AgentDto.Report.STATE),
                reportGenerator.getAgentStringColumn(AgentDto.Report.LGA),
                reportGenerator.getAgentStringColumn(AgentDto.Report.GPS_LAT),
                reportGenerator.getAgentStringColumn(AgentDto.Report.GPS_LON),
                reportGenerator.getAgentDateColumn(AgentDto.Report.DATE_CREATED)
        };
        return reportGenerator.generateAgentReport(agents, search.getDownloadType(), columns);
    }

    ByteArrayOutputStream downloadAgentManagers(ReportSearch reportSearch) {

        List<AgentManagerDto> managers = agentMgrService.findByDateRange(reportSearch.getFrom(),
                reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.FIRST_NAME),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.LAST_NAME),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.STATE),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.LGA),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.EMAIL),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.ROLE),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.BVN),
                reportGenerator.getAgentManagerStringColumn(AgentManagerDto.Report.AGENT_MANAGER_CODE),
                reportGenerator.getAgentManagerDateColumn(AgentManagerDto.Report.DATE_CREATED),
        };

        return reportGenerator.generateAgentManagerReport(managers,reportSearch.getDownloadType(),
                columns);
    }*/
}
