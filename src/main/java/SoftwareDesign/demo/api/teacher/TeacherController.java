package SoftwareDesign.demo.api.teacher;


import SoftwareDesign.demo.api.teacher.dto.ScheduledConsultationDto;
import SoftwareDesign.demo.api.teacher.dto.TeacherDashboardResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.student.service.StudentService;
import SoftwareDesign.demo.domain.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teacher")
public class TeacherController implements TeacherApi {

    private final TeacherService teacherService;
    private final StudentService studentService;

    // 대시보드 메인 데이터 (이름, 과목, 카운트 등)
    @GetMapping("/me/dashboard")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboardResponse>> getTeacherDashboard(Authentication authentication) {

        TeacherDashboardResponse response = teacherService.getTeacherDashboard(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    // 상담 예약 가져오기
    @GetMapping("/me/consultations/scheduled")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ScheduledConsultationDto>>> getScheduledConsultations(Authentication authentication) {
        List<ScheduledConsultationDto> response = teacherService.getScheduledConsultations(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    // 개별 학생 출석률 조회 (Cache Aside 적용 버전)
    @GetMapping("/students/{studentId}/attendance-rate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Double>> getStudentAttendanceRate(@PathVariable Long studentId) {
        double rate = studentService.getAttendanceRate(studentId);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, rate));
    }
}