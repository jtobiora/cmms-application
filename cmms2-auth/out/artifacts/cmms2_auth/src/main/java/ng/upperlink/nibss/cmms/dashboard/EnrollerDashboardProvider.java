package ng.upperlink.nibss.cmms.dashboard;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;

class EnrollerDashboardProvider {
/*
    private final EnrollmentReportService enrollmentReportService;
    private final EnrollerService enrollerService;
    private final ReportGenerator reportGenerator;

    EnrollerDashboardProvider(EnrollmentReportService enrollmentReportService,
                              EnrollerService enrollerService,
                              ReportGenerator reportGenerator) {

        this.enrollmentReportService = enrollmentReportService;
        this.enrollerService = enrollerService;
        this.reportGenerator = reportGenerator;
    }


    Page<EnrollmentReportDto> findForUser(UserDetail userDetail, Pageable pageable,ReportSearchWithParam reportSearch) {

        String enrollerCode = getEnrollerCode(userDetail);

        if( reportSearch.isDateEmpty()) {
            return enrollmentReportService.findByEnroller(enrollerCode, reportSearch.getParam(),pageable);
        } else {
            return enrollmentReportService.findByEnrollerAndDateRange(enrollerCode, reportSearch.getFrom(), reportSearch.getTo(), reportSearch.getParam(), pageable);
        }
    }

    private long getEnrollerId(UserDetail userDetail) {

        Long enrollerId = enrollerService.getIdForUserId(userDetail.getUserId());
        return enrollerId == null ? -1 : enrollerId;
    }

    private String getEnrollerCode(UserDetail userDetail) {

        String enrollerCode = enrollerService.getCodeForUserId(userDetail.getUserId());
        return enrollerCode == null ? " " : enrollerCode;
    }


    public ByteArrayOutputStream downloadEnrollmentReport(UserDetail userDetail, ReportSearch reportSearch) {
        String enrollerCode = getEnrollerCode(userDetail);

        List<EnrollmentReportDto> enrollments = enrollmentReportService.findByEnrollerAndDateRange(enrollerCode,
                reportSearch.getFrom(),
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
                reportGenerator.getEnrollmentReportStringColumn(EnrollmentReportDto.Report.AGENT_CODE),
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

    }*/
}
