package ng.upperlink.nibss.cmms.dashboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DashboardProvider {

/*
    private BranchService branchService;
    private AgentMgrService agentMgrService;
    private EnrollerService enrollerService;
    private AgentService agentService;
    private UserService userService;
    private EnrollmentReportService enrollmentReportService;

    private final SuperAdminDashboardProvider nibssProvider;
    private final AgentManagerDashboardProvider managerDashboardProvider;
    private final EnrollerDashboardProvider enrollerProvider;
    private final AgentDashboardProvider agentProvider;

    private AgentManagerInstitutionService agentManagerInstitutionService;

    @Autowired
    public void setAgentManagerInstitutionService(AgentManagerInstitutionService agentManagerInstitutionService) {
        this.agentManagerInstitutionService = agentManagerInstitutionService;
    }

    public DashboardProvider(BranchService branchService,
                             AgentMgrService agentMgrService,
                             EnrollerService enrollerService, AgentService agentService,
                             UserService userService,
                             EnrollmentReportService enrollmentReportService,
                             ResourceLoader resourceLoader,
                             AgentManagerInstitutionService agentManagerInstitutionService) {


        this.branchService = branchService;
        this.agentMgrService = agentMgrService;
        this.enrollerService = enrollerService;
        this.agentService = agentService;
        this.userService = userService;
        this.enrollmentReportService = enrollmentReportService;
        this.agentManagerInstitutionService = agentManagerInstitutionService;


        nibssProvider = new SuperAdminDashboardProvider(branchService, agentMgrService, enrollerService,
                agentService, userService, enrollmentReportService, new ReportGenerator(resourceLoader), agentManagerInstitutionService);

        managerDashboardProvider = new AgentManagerDashboardProvider(agentService, enrollerService,
                branchService, agentMgrService, enrollmentReportService, new ReportGenerator(resourceLoader));

        enrollerProvider = new EnrollerDashboardProvider(enrollmentReportService, enrollerService,
                new ReportGenerator(resourceLoader));

        agentProvider = new AgentDashboardProvider(branchService, enrollerService,
                agentService, enrollmentReportService, new ReportGenerator(resourceLoader));

    }

    public DashboardDto getDashboardForUser(UserDetail userDetail) {
        synchronized (new Object()) {
            UserType userType = getUserType(userDetail);
            switch (userType) {
                case NIBSS:
                    SuperAdminDashboardDto nibssDto = nibssProvider.getDashboard();
                    nibssDto.setCountryStatistic(getCountryStatistic(userDetail));

                    return nibssDto;
                case AGENT_MANAGER:
                    AgentManagerDashboardDto agentMgrDto = managerDashboardProvider.getForUser(userDetail);
                    agentMgrDto.setCountryStatistic(getCountryStatistic(userDetail));

                    return agentMgrDto;

                case AGENT:
                    return agentProvider.getForUser(userDetail);
                default:
                    return new DashboardDto();
            }
        }
    }


    public Page<BranchDto> getBranchesForUser(UserDetail userDetail, ReportSearch reportSearch) {
        synchronized (new Object()) {
            Pageable pageable = getPageable(reportSearch);

            UserType userType = getUserType(userDetail);

            switch (userType) {
                case NIBSS:
                    return nibssProvider.getBranchesForNibss(pageable, reportSearch);
                case AGENT_MANAGER:
                    return managerDashboardProvider.getBranchesForUser(userDetail, pageable, reportSearch);
                case AGENT:
                    return agentProvider.getBranches(userDetail, pageable, reportSearch);

                default:
                    throw new IllegalArgumentException("method not available to user type " + userType);
            }
        }
    }

    private UserType getUserType(UserDetail userDetail) {
        return UserType.find(userDetail.getUserType());
    }

    private Pageable getPageable(BaseSearch search) {
        return new PageRequest(search.getDataPageNo(), search.getDataSize());
    }

    public Page<EnrollerDto> getEnrollersForUser(UserDetail userDetail, ReportSearch reportSearch) {
        synchronized (new Object()) {
            Pageable pageable = getPageable(reportSearch);
            switch (getUserType(userDetail)) {
                case NIBSS:
                    return nibssProvider.getEnrollersForNibss(pageable, reportSearch);
                case AGENT_MANAGER:
                    return managerDashboardProvider.getEnrollers(userDetail, pageable, reportSearch);
                case AGENT:
                    return agentProvider.getEnrollers(userDetail, pageable, reportSearch);

                default:
                    throw new IllegalArgumentException("method available to NIBSS and AGENT_MANAGER");

            }
        }
    }

    public Page<EnrollmentReportDto> getEnrollmentsForUser(UserDetail userDetail, ReportSearchWithParam reportSearch) {
        synchronized (new Object()) {
            Pageable pageable = getPageable(reportSearch);

            switch (getUserType(userDetail)) {
                case NIBSS:
                    return nibssProvider.getEnrollmentReports(pageable, reportSearch);
                case AGENT_MANAGER:
                    return managerDashboardProvider.getEnrollmentReports(userDetail, pageable, reportSearch);
                case ENROLLER:
                    return enrollerProvider.findForUser(userDetail, pageable, reportSearch);
                case AGENT:
                    return agentProvider.getEnrollmentReports(userDetail, pageable, reportSearch);
                    default:
                        throw new IllegalArgumentException("method not available user type " + getUserType(userDetail));
            }
        }
    }

    public Optional<ByteArrayOutputStream> downloadBranches(UserDetail userDetail, ReportSearch reportSearch) {
        synchronized (new Object()) {

            checkReportDateEmpty(reportSearch);

            ByteArrayOutputStream outputStream;

            switch (getUserType(userDetail)) {
                case AGENT_MANAGER:
                    outputStream =  managerDashboardProvider.downloadBranches(userDetail, reportSearch);
                    break;
                case NIBSS:
                    outputStream =  nibssProvider.downloadBranches(reportSearch);
                    break;
                case AGENT:
                    outputStream = agentProvider.downloadBranches(userDetail, reportSearch);
                    break;
                default:
                    throw new IllegalArgumentException("method not available to user type " + getUserType(userDetail));
            }

            return Optional.ofNullable( checkEmptyStream(outputStream));
        }
    }

    private ByteArrayOutputStream checkEmptyStream(ByteArrayOutputStream outputStream) {
        outputStream = null == outputStream || outputStream.size() == 0 ? null : outputStream;
        return outputStream;
    }

    public Optional<ByteArrayOutputStream> downloadEnrollers(UserDetail userDetail, ReportSearch reportSearch) {
        synchronized (new Object()) {
            checkReportDateEmpty(reportSearch);

            ByteArrayOutputStream outputStream;

            switch (getUserType(userDetail)) {
                case AGENT_MANAGER:
                    outputStream = managerDashboardProvider.downloadEnrollers(userDetail, reportSearch);
                    break;
                case NIBSS:
                    outputStream = nibssProvider.downloadEnrollers(reportSearch);
                    break;
                case AGENT:
                    outputStream = agentProvider.downloadEnrollers(userDetail, reportSearch);
                    break;
                default:
                    throw new IllegalArgumentException("method not available to user type " + getUserType(userDetail));
            }

            return Optional.ofNullable( checkEmptyStream(outputStream));
        }
    }


    public Optional<ByteArrayOutputStream> downloadEnrollmentReports(UserDetail userDetail, ReportSearch reportSearch) {
        synchronized (new Object()) {

            checkReportDateEmpty(reportSearch);

            ByteArrayOutputStream outputStream;

            switch (getUserType(userDetail)) {
                case AGENT_MANAGER:
                    outputStream = managerDashboardProvider.downloadEnrollmentReport(userDetail, reportSearch);
                    break;
                case NIBSS:
                    outputStream = nibssProvider.downloadEnrollmentReport(reportSearch);
                    break;
                case ENROLLER:
                    outputStream = enrollerProvider.downloadEnrollmentReport(userDetail, reportSearch);
                    break;
                case AGENT:
                    outputStream = agentProvider.downloadEnrollmentReport(userDetail, reportSearch);
                    break;
                default:
                    throw new IllegalArgumentException("method not available to user type " + getUserType(userDetail));
            }

            return Optional.ofNullable( checkEmptyStream(outputStream));
        }
    }


    private void checkReportDateEmpty(ReportSearch reportSearch) {
        if(reportSearch.isDateEmpty())
            throw new IllegalStateException("from and to dates are required for report download");
    }
    private List<EnrollmentStatistic> getCountryStatistic(UserDetail userDetail) {
        try {
            return enrollmentReportService.getEnrollmentStaticsForCountry(userDetail);
        } catch (RuntimeException e) {
            log.error("could not get country statistic", e);
            return Collections.emptyList();
        }
    }

    public Page<AgentManagerDto> getAgentManagers(UserDetail userDetail, ReportSearch search) {
        synchronized (new Object()) {
            Pageable pageable = getPageable(search);

            switch (getUserType(userDetail)) {
                case NIBSS:
                    return nibssProvider.getAgentManagers(search, pageable);
                    default:
                        throw new IllegalArgumentException("ONLY Permitted to NIBSS");
            }


        }
    }

    public Page<AgentDto> getAgents(UserDetail userDetail, ReportSearch search) {
        synchronized (new Object()) {
            Pageable pageable = getPageable(search);

            switch (getUserType(userDetail)) {
                case NIBSS:
                    return nibssProvider.getAgents(search, pageable);
                case AGENT_MANAGER:
                    return managerDashboardProvider.getAgents(userDetail,search,pageable);
                    default:
                        throw new IllegalArgumentException("not permitted for user " + getUserType(userDetail));
            }
        }
    }

    public Optional<ByteArrayOutputStream> downloadAgents(UserDetail userDetail, ReportSearch search) {
        synchronized (new Object()) {
            checkReportDateEmpty(search);

            ByteArrayOutputStream outputStream;

            switch (getUserType(userDetail)) {
                case NIBSS:
                    outputStream = nibssProvider.downloadAgents(search);
                    break;
                case AGENT_MANAGER:
                    outputStream = managerDashboardProvider.downloadAgents(userDetail, search);
                    break;
                    default:
                        throw new IllegalArgumentException("ONLY Permitted to NIBSS and AGENT_MANAGER");
            }

            return Optional.ofNullable( checkEmptyStream(outputStream));

        }
    }

    public Optional<ByteArrayOutputStream> downloadAgentManagers(UserDetail userDetail,ReportSearch search) {
        synchronized (new Object()) {
            if(getUserType(userDetail) != UserType.NIBSS)
                throw new IllegalArgumentException("Only permitted for NIBSS user");

            ByteArrayOutputStream outputStream = nibssProvider.downloadAgentManagers(search);

            return Optional.ofNullable(outputStream);
        }
    }*/
}
