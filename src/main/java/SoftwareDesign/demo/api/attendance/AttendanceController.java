package SoftwareDesign.demo.api.attendance;

import SoftwareDesign.demo.api.attendance.dto.AttendanceRequest;
import SoftwareDesign.demo.api.attendance.dto.AttendanceSummaryResponse;
import SoftwareDesign.demo.api.attendance.dto.AttendanceUpdateRequest;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.attendance.service.AttendanceService;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController implements AttendanceApi {

    private final AttendanceService attendanceService;

    @Override
    @PostMapping("/single")
    @PreAuthorize("hasRole('TEACHER')") // 단일 오늘 출석 처리
    public ResponseEntity<ApiResponse<String>> markSingleAttendance(@RequestBody AttendanceRequest request) {

        attendanceService.markAttendance(request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATE_SUCCESS, "출석 정보가 반영"));
    }

    @Override
    @PatchMapping("/update/{attendanceId}")
    @PreAuthorize("hasRole('TEACHER')") // 단일 출석 처리
    public ResponseEntity<ApiResponse<String>> updateRecord(@PathVariable Long attendanceId, @RequestBody AttendanceUpdateRequest request){

        attendanceService.updateAttendanceRecord(attendanceId,request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS, "출석 정보 수정완료"));
    }

    @Override
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('TEACHER')") // 대량 출석 처리
    public ResponseEntity<ApiResponse<String>> markBulkAttendance(
            @RequestBody List<AttendanceRequest> requests) {

        attendanceService.markBulkAttendance(requests);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS, "전원 출석 처리"));
    }

    @Override
    @GetMapping("/rate/{studentId}")
    public ResponseEntity<ApiResponse<Double>> getAttendanceRate(@PathVariable Long studentId) {
        double rate = attendanceService.calculateAttendanceRate(studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, rate));
    }

    @Override
    @GetMapping("/unmarked")
    @PreAuthorize("hasRole('TEACHER')") // "누가 안 왔나"는 선생님만 보는거
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getUnmarkedStudents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<StudentResponse> response = attendanceService.getUnmarkedStudents(targetDate);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    @Override
    @GetMapping("/report/{studentId}")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getMonthlyReport(
            @PathVariable Long studentId,
            @RequestParam int year,
            @RequestParam int month) {

        AttendanceSummaryResponse response = attendanceService.getMonthlyReport(studentId, year, month);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }
}