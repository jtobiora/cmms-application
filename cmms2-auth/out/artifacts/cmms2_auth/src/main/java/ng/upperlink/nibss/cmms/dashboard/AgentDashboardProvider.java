package ng.upperlink.nibss.cmms.dashboard;

import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
class AgentDashboardProvider {
/*

    private final BranchService branchService;
    private final EnrollerService enrollerService;
    private final AgentService agentService;
    private final EnrollmentReportService enrollmentReportService;
    private final ReportGenerator reportGenerator;

    AgentDashboardProvider(BranchService branchService,
                            EnrollerService enrollerService,
                            AgentService agentService,
                           EnrollmentReportService enrollmentReportService,
                           ReportGenerator reportGenerator) {

        this.branchService = branchService;
        this.enrollerService = enrollerService;
        this.agentService = agentService;
        this.enrollmentReportService = enrollmentReportService;
        this.reportGenerator = reportGenerator;
    }

    AgentDashboardDto getForUser(UserDetail userDetail) {
        AgentDashboardDto dto = new AgentDashboardDto();

        try {
            Long agentId = getAgentId(userDetail);
            if( null != agentId) {
                dto.setBranchCount(branchService.countByAgent(agentId));
                dto.setEnrollerCount(enrollerService.countByAgent(agentId));
            }
        } catch (RuntimeException e) {
            log.error("could not get dashboard",e);
        }

        return dto;
    }

    private Long getAgentId(UserDetail userDetail) {
        Long agentId =  agentService.getIdFromUserId(userDetail.getUserId());
        return agentId == null ? -1 : agentId;
    }

    private String getAgentCode(UserDetail userDetail) {
        String agentCode =  agentService.getCodeFromUserId(userDetail.getUserId());
        return agentCode == null ? " " : agentCode;
    }

     Page<EnrollerDto> getEnrollers(UserDetail userDetail, Pageable pageable, ReportSearch reportSearch) {
         long agentId = getAgentId(userDetail);
         if(reportSearch.isDateEmpty()) {
             return enrollerService.findByAgent(agentId, pageable);
         } else {
             return enrollerService.findByAgentAndDateRange(agentId, reportSearch.getFrom(), reportSearch.getTo(), pageable);
         }
    }


     ByteArrayOutputStream downloadEnrollers(UserDetail userDetail, ReportSearch reportSearch) {
         long agentId = getAgentId(userDetail);

         List<EnrollerDto> enrollers = enrollerService.findByAgentAndDateRange(agentId, reportSearch.getFrom(),
                 reportSearch.getTo());

         TextColumnBuilder[] columns = {
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.FIRST_NAME),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LAST_NAME),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.EMAIL),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.BVN),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.ROLE),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.ENROLLER_CODE),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LGA),
                 reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.STATE),
                 reportGenerator.getEnrollerDateColumn(EnrollerDto.Report.DATE_CREATED)
         };

         return reportGenerator.generateEnrollerReport(enrollers, reportSearch.getDownloadType(),
                 columns);
    }

     Page<BranchDto> getBranches(UserDetail userDetail, Pageable pageable, ReportSearch reportSearch) {

        long agentId = getAgentId(userDetail);

        if( reportSearch.isDateEmpty()) {
            return branchService.findByAgent(agentId, pageable);
        } else {
            return branchService.findByAgentAndDateRange(agentId, reportSearch.getFrom(),
                    reportSearch.getTo(), pageable);
        }
    }

     ByteArrayOutputStream downloadBranches(UserDetail userDetail, ReportSearch reportSearch) {
        long agentId = getAgentId(userDetail);

        List<BranchDto> branches = branchService.findByAgentAndDateRange(agentId, reportSearch.getFrom(),
                reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getBranchStringColumn(BranchDto.Report.BRANCH_NAME),
                reportGenerator.getBranchStringColumn(BranchDto.Report.CITY),
                reportGenerator.getBranchStringColumn(BranchDto.Report.STATE),
                reportGenerator.getBranchDateColumn(BranchDto.Report.DATE_CREATED)
        };

        return reportGenerator.generateBranchReport(branches, reportSearch.getDownloadType(),
                columns);
    }

     ByteArrayOutputStream downloadEnrollmentReport(UserDetail userDetail, ReportSearch reportSearch) {
        String agentCode = getAgentCode(userDetail);

        List<EnrollmentReportDto> enrollments = enrollmentReportService
                .findByAgentAndDateRange(agentCode, reportSearch.getFrom(),
                        reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.TICKET_NUMBER),
                reportGenerator.getEnrollmentReportDateColumn(EnrollmentReportDto.Report.CAPTURE_DATE),
                reportGenerator.getEnrollmentReportDateColumn(EnrollmentReportDto.Report.SYNC_DATE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.LATITUDE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.LONGITUDE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.CITY),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.REGION),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.COUNTRY),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.ENROLLER_CODE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.VALIDATION_STATUS),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.FINGERPRINT_SCANNER),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.VALIDATION_MESSAGE),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.BVN),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.BMS_TICKET_ID),
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.RESPONSE_CODE),
                reportGenerator.getEnrollmentReportAmountColumn(EnrollmentReportDto.Report.AMOUNT)
        };

        return reportGenerator.generateEnrollmentReport(enrollments, reportSearch.getDownloadType(),
                columns);

    }

     Page<EnrollmentReportDto> getEnrollmentReports(UserDetail userDetail, Pageable pageable, ReportSearchWithParam reportSearch) {

        String agentCode = getAgentCode(userDetail);

        if(reportSearch.isDateEmpty()) {
            return enrollmentReportService.findByAgent(agentCode,reportSearch.getParam(), pageable);
        } else {
            return enrollmentReportService.findByAgentAndDateRange(agentCode, reportSearch.getFrom(), reportSearch.getTo(), reportSearch.getParam(), pageable);
        }
    }*/
}
