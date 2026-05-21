package SoftwareDesign.demo.api.report;

import SoftwareDesign.demo.api.report.dto.ReportFormat;
import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Report", description = "보고서 다운로드 API")
public interface ReportApi {

    @Operation(summary = "성적 보고서 다운로드", description = "특정 학생의 학기별 성적 보고서를 PDF 또는 Excel 파일로 다운로드합니다.")
    ResponseEntity<byte[]> downloadGradeReport(
            Long studentId,
            String semester,
            ReportFormat format,
            Authentication authentication
    );

    @Operation(summary = "상담 내역 보고서 다운로드", description = "특정 학생의 상담 내역 보고서를 PDF 또는 Excel 파일로 다운로드합니다.")
    ResponseEntity<byte[]> downloadConsultationReport(
            Long studentId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            String keyword,
            ReportFormat format,
            Authentication authentication
    );

    @Operation(summary = "피드백 요약 보고서 다운로드", description = "특정 학생의 피드백 요약 보고서를 PDF 또는 Excel 파일로 다운로드합니다.")
    ResponseEntity<byte[]> downloadFeedbackReport(
            Long studentId,
            FeedbackCategory category,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            String keyword,
            ReportFormat format,
            Authentication authentication
    );
}
