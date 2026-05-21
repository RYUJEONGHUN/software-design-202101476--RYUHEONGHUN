package SoftwareDesign.demo.domain.report.service;

import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackSearchCondition;
import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.report.dto.ReportFormat;
import SoftwareDesign.demo.domain.consultation.service.ConsultationService;
import SoftwareDesign.demo.domain.feedback.service.FeedbackService;
import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import SoftwareDesign.demo.domain.grade.service.GradeService;
import SoftwareDesign.demo.domain.report.generator.ExcelReportGenerator;
import SoftwareDesign.demo.domain.report.generator.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final MediaType PDF_MEDIA_TYPE = MediaType.APPLICATION_PDF;

    private final GradeService gradeService;
    private final ConsultationService consultationService;
    private final FeedbackService feedbackService;
    private final ExcelReportGenerator excelReportGenerator;
    private final PdfReportGenerator pdfReportGenerator;

    public ReportFile generateGradeReport(Long studentId, String semester, ReportFormat format) {
        GradeChartResponse report = gradeService.getGradeChart(studentId, semester);

        if (format == ReportFormat.PDF) {
            return new ReportFile(
                    "grade-report-" + studentId + "-" + semester + ".pdf",
                    PDF_MEDIA_TYPE,
                    pdfReportGenerator.generateGradeReport(report)
            );
        }

        return new ReportFile(
                "grade-report-" + studentId + "-" + semester + ".xlsx",
                EXCEL_MEDIA_TYPE,
                excelReportGenerator.generateGradeReport(report)
        );
    }

    public ReportFile generateConsultationReport(Long studentId, LocalDate startDate,
                                                 LocalDate endDate, String keyword,
                                                 ReportFormat format) {
        ConsultationSearchCondition condition = new ConsultationSearchCondition();
        condition.setStudentId(studentId);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);
        condition.setKeyword(keyword);

        List<ConsultationResponse> report = consultationService.searchConsultations(condition);

        if (format == ReportFormat.PDF) {
            return new ReportFile(
                    "consultation-report-" + studentId + ".pdf",
                    PDF_MEDIA_TYPE,
                    pdfReportGenerator.generateConsultationReport(report)
            );
        }

        return new ReportFile(
                "consultation-report-" + studentId + ".xlsx",
                EXCEL_MEDIA_TYPE,
                excelReportGenerator.generateConsultationReport(report)
        );
    }

    public ReportFile generateFeedbackReport(Long studentId, FeedbackCategory category,
                                             LocalDate startDate, LocalDate endDate,
                                             String keyword, ReportFormat format) {
        FeedbackSearchCondition condition = new FeedbackSearchCondition();
        condition.setCategory(category);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);
        condition.setKeyword(keyword);

        List<FeedbackResponse> report = feedbackService.getFeedbacksForStaff(studentId, condition);

        if (format == ReportFormat.PDF) {
            return new ReportFile(
                    "feedback-report-" + studentId + ".pdf",
                    PDF_MEDIA_TYPE,
                    pdfReportGenerator.generateFeedbackReport(report)
            );
        }

        return new ReportFile(
                "feedback-report-" + studentId + ".xlsx",
                EXCEL_MEDIA_TYPE,
                excelReportGenerator.generateFeedbackReport(report)
        );
    }
}
