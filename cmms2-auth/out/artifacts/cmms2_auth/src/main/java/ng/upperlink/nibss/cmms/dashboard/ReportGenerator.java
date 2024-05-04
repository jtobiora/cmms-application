package ng.upperlink.nibss.cmms.dashboard;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class ReportGenerator {
/*
    private final ResourceLoader resourceLoader;

    ReportGenerator(ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;
    }

    ByteArrayOutputStream generateBranchReport(List<BranchDto> branches,
                                               DownloadType downloadType,
                                               TextColumnBuilder<?>... columns) {

        if( null == branches || branches.isEmpty())
            return null;

        JasperReportBuilder reportBuilder = report()
                .columns(columns)
                .setDataSource(branches);

        return handleExport(reportBuilder, downloadType);
    }


    ByteArrayOutputStream generateEnrollerReport(List<EnrollerDto> enrollers,
                                               DownloadType downloadType,
                                               TextColumnBuilder<?>... columns) {

        if( null == enrollers || enrollers.isEmpty())
            return null;

        JasperReportBuilder reportBuilder = report()
                .columns(columns)
                .setDataSource(enrollers);

        return handleExport(reportBuilder, downloadType);

    }

    ByteArrayOutputStream generateEnrollmentReport(List<EnrollmentReportDto> enrollments,
                                                 DownloadType downloadType,
                                                   TextColumnBuilder<?>... columns) {

        if( null == enrollments || enrollments.isEmpty())
            return null;

        JasperReportBuilder reportBuilder = report()
                .columns(columns)
                .setDataSource(enrollments);

        return handleExport(reportBuilder, downloadType);

    }

    ByteArrayOutputStream generateAgentReport(List<AgentDto> agents, DownloadType downloadType,
                                              TextColumnBuilder<?>... columns) {
        if(null == agents || agents.isEmpty())
            return null;
        JasperReportBuilder reportBuilder = report()
                .columns(columns)
                .setDataSource(agents);
        return handleExport(reportBuilder, downloadType);
    }

    ByteArrayOutputStream generateAgentManagerReport(List<AgentManagerDto> managers,
                                                     DownloadType downloadType,
                                                     TextColumnBuilder<?>... columns) {

        if(null == managers || managers.isEmpty())
            return null;

        JasperReportBuilder reportBuilder = report()
                .columns(columns)
                .setDataSource(managers);
        return handleExport(reportBuilder, downloadType);
    }


    private ByteArrayOutputStream handleExport(JasperReportBuilder reportBuilder, DownloadType downloadType) {
       switch (downloadType) {
            default:
            case CSV:
                return exportToCsv(reportBuilder);
            case PDF:
                return exportToPdf(reportBuilder);
            case EXCEL:
                return exportToExcel(reportBuilder);
        }
    }


    private ByteArrayOutputStream exportToCsv(JasperReportBuilder reportBuilder) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       try {
           reportBuilder.ignorePagination().toCsv(outputStream);
           return outputStream;
       } catch (DRException e) {
           throw  new RuntimeException("could not export report to csv",e);
       }
    }

    private ByteArrayOutputStream exportToExcel(JasperReportBuilder reportBuilder) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            reportBuilder.ignorePagination().toXlsx(outputStream);
            return outputStream;
        } catch (DRException e) {
            e.printStackTrace();
            throw  new RuntimeException("could not export report to excel",e);
        }
    }


    private ByteArrayOutputStream exportToPdf(JasperReportBuilder reportBuilder) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StyleBuilder footerStyle = stl.style().setFontSize(5).setForegroundColor(new Color(210, 210, 210));
        try {

            StyleBuilder bodyStyle = stl.style().setFontSize(7).setPadding(3);

            StyleBuilder columnHeaderStyle = stl.style().setFontSize(8)
                    .setBorder(stl.pen1Point()).setBackgroundColor(Color.LIGHT_GRAY)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).setLeftPadding(4);

            Resource logoResource = resourceLoader.getResource("classpath:static/image/nibss.png");
            InputStream logoStream = logoResource.getInputStream();

            if( null != logoStream) {
                reportBuilder.title(
                        cmp.horizontalList()
                        .add(
                                cmp.image(ImageIO.read(logoStream)).setFixedDimension(200, 90)
                                .setHorizontalImageAlignment(HorizontalImageAlignment.LEFT)
                        ).newRow().add(cmp.filler().setFixedHeight(10)
                ) );
            }

            reportBuilder.pageFooter(
                    cmp.horizontalList().add(
                            cmp.text(getReportFooter())
                                    .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)
                            .setStyle(footerStyle),
                            cmp.pageXofY().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                            .setStyle(footerStyle)
                    )
            );

            reportBuilder.setColumnHeaderStyle(columnHeaderStyle)
                    .setColumnStyle(bodyStyle)
                    .highlightDetailEvenRows()
                    .toPdf(outputStream);
            return outputStream;
        } catch (DRException | IOException e) {
            throw  new RuntimeException("could not export report to pdf",e);
        }
    }

     TextColumnBuilder<String> getStringColumn(String label, String propertyName) {
        return col.column(label, propertyName, type.stringType());
    }

     TextColumnBuilder<Date> getDateColumn(String label, String propertyName) {
         return col.column(label, propertyName, type.dateType());
    }

     TextColumnBuilder<BigDecimal> getAmountColumn(String label, String propertyName) {
        return col.column(label, propertyName, type.bigDecimalType());
    }


    TextColumnBuilder<String> getBranchStringColumn(BranchDto.Report column) {
        return getStringColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<Date> getBranchDateColumn(BranchDto.Report column) {
        return getDateColumn(column.getLabel(), column.getPropertyName());
    }


    TextColumnBuilder<String> getEnrollerStringColumn(EnrollerDto.Report column) {
        return getStringColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<Date> getEnrollerDateColumn(EnrollerDto.Report column) {
        return getDateColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<String> getEnrollmentReportStringColumn(EnrollmentReportDto.Report column) {
        return getStringColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<Date> getEnrollmentReportDateColumn(EnrollmentReportDto.Report column) {
        return getDateColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<BigDecimal> getEnrollmentReportAmountColumn(EnrollmentReportDto.Report column) {
        return getAmountColumn(column.getLabel(), column.getPropertyName());
    }


    TextColumnBuilder<String> getAgentStringColumn(AgentDto.Report column) {
        return getStringColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<Date> getAgentDateColumn(AgentDto.Report column) {
        return getDateColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<String> getAgentManagerStringColumn(AgentManagerDto.Report column) {
        return getStringColumn(column.getLabel(), column.getPropertyName());
    }

    TextColumnBuilder<Date> getAgentManagerDateColumn(AgentManagerDto.Report column) {
        return getDateColumn(column.getLabel(), column.getPropertyName());
    }

    private String getReportFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("\u00A9").append(LocalDate.now().getYear())
                .append(". Nigeria Inter-Bank Settlement System Plc.")
                .append(" Generated: ")
                .append(new SimpleDateFormat("yyyy-MM-dd h:mm a").format(new Date()));

        return builder.toString();
    }*/
}
