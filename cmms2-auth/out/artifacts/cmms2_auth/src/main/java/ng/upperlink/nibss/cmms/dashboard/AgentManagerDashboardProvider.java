package ng.upperlink.nibss.cmms.dashboard;

import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
class AgentManagerDashboardProvider {

  /*  private AgentService agentService;
    private EnrollerService enrollerService;
    private BranchService branchService;
    private AgentMgrService agentMgrService;
    private final EnrollmentReportService enrollmentReportService;

    private final ReportGenerator reportGenerator;

    AgentManagerDashboardProvider(AgentService agentService,
                                  EnrollerService enrollerService,
                                  BranchService branchService,
                                  AgentMgrService agentMgrService,
                                  EnrollmentReportService enrollmentReportService,
                                  ReportGenerator reportGenerator) {

        this.agentService = agentService;
        this.enrollerService = enrollerService;
        this.branchService = branchService;
        this.agentMgrService = agentMgrService;
        this.enrollmentReportService = enrollmentReportService;
        this.reportGenerator = reportGenerator;
    }

    AgentManagerDashboardDto getForUser(UserDetail userDetail) {
        AgentManagerDashboardDto dto = new AgentManagerDashboardDto();

        try {
//            Long managerId = agentMgrService.getIdFromUserId(userDetail.getUserId());
            Long managerInstitutionId = agentMgrService.getInstitutionIdFromUserId(userDetail.getUserId());
            if (null != managerInstitutionId) {

                dto.setAgentCount(agentService.countByAgentManagerInstitution(managerInstitutionId));
                dto.setEnrollerCount(enrollerService.countByAgentManagerInstitution(managerInstitutionId));
                dto.setBranchCount(branchService.countByAgentManagerInstitution(managerInstitutionId));
            }

        } catch (RuntimeException e) {
            log.error("could not get dashboard info", e);
        }

        return dto;
    }

    Page<BranchDto> getBranchesForUser(UserDetail userDetail, Pageable pageable, ReportSearch reportSearch) {

        Long mgrId = getManagerInstitutionId(userDetail);

        if (reportSearch.isDateEmpty()) {
            return branchService.findByAgentManagerInstitution(mgrId, pageable);
        } else {
            return branchService.findByAgentManagerInstitutionAndDateRange(mgrId, reportSearch.getFrom(), reportSearch.getTo(),
                    pageable);
        }
    }


    Page<EnrollerDto> getEnrollers(UserDetail userDetail, Pageable pageable, ReportSearch reportSearch) {
        Long mgrId = getManagerInstitutionId(userDetail);
        if (reportSearch.isDateEmpty()) {
            return enrollerService.findByAgentManagerInstitution(mgrId, pageable);
        } else {
            return enrollerService.findByAgentManagerInstitutionAndDateRange(mgrId, reportSearch.getFrom(),
                    reportSearch.getTo(), pageable);
        }
    }

    Page<EnrollmentReportDto> getEnrollmentReports(UserDetail userDetail, Pageable pageable,
                                                   ReportSearchWithParam reportSearch) {

        String mgrInstitutionCode = getManagerInstitutionCode(userDetail);

        if (reportSearch.isDateEmpty()) {
            return enrollmentReportService.findByAgentManagerInstitution(mgrInstitutionCode,reportSearch.getParam(), pageable);
        } else {
            return enrollmentReportService.findByAgentManagerInstitutionAndDateRange(mgrInstitutionCode, reportSearch.getFrom(), reportSearch.getTo(), reportSearch.getParam(), pageable);
        }
    }

    public ByteArrayOutputStream downloadBranches(UserDetail userDetail, ReportSearch reportSearch) {

        Long mgrId = getManagerInstitutionId(userDetail);

        List<BranchDto> branches = branchService.findByAgentManagerInstitutionAndDateRange(mgrId, reportSearch.getFrom(),
                reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getBranchStringColumn(BranchDto.Report.BRANCH_NAME),
                reportGenerator.getBranchStringColumn(BranchDto.Report.CITY),
                reportGenerator.getBranchStringColumn(BranchDto.Report.STATE),
                reportGenerator.getBranchStringColumn(BranchDto.Report.AGENT_CODE),
                reportGenerator.getBranchDateColumn(BranchDto.Report.DATE_CREATED)
        };

        return reportGenerator.generateBranchReport(branches, reportSearch.getDownloadType(),
                columns);

    }

*//*    private Long getManagerId(UserDetail userDetail) {
        Long mgrId = agentMgrService.getIdFromUserId(userDetail.getUserId());
        mgrId = mgrId == null ? -1 : mgrId;
        return mgrId;
    }*//*

    private Long getManagerInstitutionId(UserDetail userDetail) {
        Long mgrInstitutionId = agentMgrService.getInstitutionIdFromUserId(userDetail.getUserId());
        mgrInstitutionId = mgrInstitutionId == null ? -1 : mgrInstitutionId;
        return mgrInstitutionId;
    }

    private String getManagerInstitutionCode(UserDetail userDetail) {
        String mgrInstitutionCode = agentMgrService.getInstitutionCodeFromUserId(userDetail.getUserId());
        mgrInstitutionCode = mgrInstitutionCode == null ? " " : mgrInstitutionCode;
        return mgrInstitutionCode;
    }


    public ByteArrayOutputStream downloadEnrollers(UserDetail userDetail, ReportSearch reportSearch) {

        long mgrId = getManagerInstitutionId(userDetail);

        List<EnrollerDto> enrollers = enrollerService.findByAgentManagerInstitutionAndDateRange(mgrId,
                reportSearch.getFrom(), reportSearch.getTo());

        TextColumnBuilder[] columns = {
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.FIRST_NAME),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LAST_NAME),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.EMAIL),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.BVN),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.ROLE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.ENROLLER_CODE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.AGENT_CODE),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.LGA),
                reportGenerator.getEnrollerStringColumn(EnrollerDto.Report.STATE),
                reportGenerator.getEnrollerDateColumn(EnrollerDto.Report.DATE_CREATED)
        };

        return reportGenerator.generateEnrollerReport(enrollers, reportSearch.getDownloadType(),
                columns);
    }

    public ByteArrayOutputStream downloadEnrollmentReport(UserDetail userDetail, ReportSearch reportSearch) {
        String mgrInstitutionCode = getManagerInstitutionCode(userDetail);

        List<EnrollmentReportDto> enrollmentReports = enrollmentReportService
                .findByAgentManagerInstitutionAndDateRange(mgrInstitutionCode, reportSearch.getFrom(), reportSearch.getTo());

        if(null == enrollmentReports || enrollmentReports.isEmpty())
            return null;

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

     Page<AgentDto> getAgents(UserDetail userDetail,ReportSearch search, Pageable pageable) {
        long mgrId = getManagerInstitutionId(userDetail);

        if(search.isDateEmpty()) {
            return agentService.findByAgentManagerInstitution(mgrId, pageable);
        } else {
            return agentService.findByAgentManagerInstitutionAndDateRange(mgrId, search.getFrom(), search.getTo(), pageable);
        }
    }

     ByteArrayOutputStream downloadAgents(UserDetail userDetail, ReportSearch search) {
         long mgrId = getManagerInstitutionId(userDetail);
         List<AgentDto> agents = agentService.findByAgentManagerInstitutionAndDateRange(mgrId, search.getFrom(), search.getTo());

         if( null == agents || agents.isEmpty())
             return null;

         TextColumnBuilder[] columns = {
                 reportGenerator.getAgentStringColumn(AgentDto.Report.AGENT_CODE),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.FIRST_NAME),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.LAST_NAME),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.STATE),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.LGA),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.EMAIL),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.EMAIL),
                 reportGenerator.getAgentStringColumn(AgentDto.Report.BVN),

                 reportGenerator.getAgentDateColumn(AgentDto.Report.DATE_CREATED)
         };

         return reportGenerator.generateAgentReport(agents, search.getDownloadType(), columns);
    }*/
}
