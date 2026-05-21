package SoftwareDesign.demo.api.report;

import SoftwareDesign.demo.api.report.dto.ReportFormat;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import SoftwareDesign.demo.domain.parent.service.ParentService;
import SoftwareDesign.demo.domain.report.service.ReportFile;
import SoftwareDesign.demo.domain.report.service.ReportService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final ReportService reportService;
    private final UserRepository userRepository;
    private final ParentService parentService;

    @Override
    @GetMapping("/grades")
    public ResponseEntity<byte[]> downloadGradeReport(
            @RequestParam Long studentId,
            @RequestParam String semester,
            @RequestParam(defaultValue = "EXCEL") ReportFormat format,
            Authentication authentication
    ) {
        validateGradeReportAccess(studentId, authentication);

        ReportFile reportFile = reportService.generateGradeReport(studentId, semester, format);
        return buildFileResponse(reportFile);
    }

    @Override
    @GetMapping("/consultations")
    public ResponseEntity<byte[]> downloadConsultationReport(
            @RequestParam Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "EXCEL") ReportFormat format,
            Authentication authentication
    ) {
        validateStaffReportAccess(authentication);

        ReportFile reportFile = reportService.generateConsultationReport(
                studentId, startDate, endDate, keyword, format);
        return buildFileResponse(reportFile);
    }

    @Override
    @GetMapping("/feedbacks")
    public ResponseEntity<byte[]> downloadFeedbackReport(
            @RequestParam Long studentId,
            @RequestParam(required = false) FeedbackCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "EXCEL") ReportFormat format,
            Authentication authentication
    ) {
        validateStaffReportAccess(authentication);

        ReportFile reportFile = reportService.generateFeedbackReport(
                studentId, category, startDate, endDate, keyword, format);
        return buildFileResponse(reportFile);
    }

    private ResponseEntity<byte[]> buildFileResponse(ReportFile reportFile) {
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(reportFile.filename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(reportFile.mediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(reportFile.content());
    }

    private void validateGradeReportAccess(Long studentId, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == UserRole.STUDENT) {
            if (!user.getId().equals(studentId)) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
            return;
        }

        if (user.getRole() == UserRole.PARENT) {
            if (!parentService.isMyChild(email, studentId)) {
                throw new CustomException(ErrorCode.NOT_YOUR_CHILD);
            }
            return;
        }

        if (user.getRole() != UserRole.TEACHER && user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateStaffReportAccess(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != UserRole.TEACHER && user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
