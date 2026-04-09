package SoftwareDesign.demo.api.attendance;

import SoftwareDesign.demo.api.attendance.dto.AttendanceRequest;
import SoftwareDesign.demo.api.attendance.dto.AttendanceSummaryResponse;
import SoftwareDesign.demo.api.attendance.dto.AttendanceUpdateRequest;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Attendance", description = "출석 관리 API")
public interface AttendanceApi {

    @Operation(summary = "오늘 자 출석 등록", description = "학생의 오늘 날짜 출석 기록을 새로 생성.")
    @ApiResponse(responseCode = "200", description = "출석 처리 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> markSingleAttendance(
            @RequestBody AttendanceRequest request);

    @Operation(summary = "출석 기록 수정", description = "과거를 포함한 특정 출석 기록의 상태를 수정하네.")
    @ApiResponse(responseCode = "200", description = "출석기록 수정 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> updateRecord(
            @PathVariable Long attendanceId,
            @RequestBody AttendanceUpdateRequest request);

    @Operation(summary = "일괄 출석 체크", description = "선생님이 여러 학생의 출석 상태를 한 번에 등록하거나 수정.")
    @ApiResponse(responseCode = "200", description = "일괄 출석 처리 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> markBulkAttendance(
            @RequestBody List<AttendanceRequest> requests);

    @Operation(summary = "학생별 출석률 조회", description = "특정 학생의 전체 출석률(%)을 계산해서 반환.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<Double>> getAttendanceRate(
            @Parameter(description = "학생 ID") Long studentId);

    @Operation(summary = "미출석자 명단 조회", description = "특정 날짜에 아직 출석 체크가 되지 않은 학생 목록을 가져오네.")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<List<StudentResponse>>> getUnmarkedStudents(
            @Parameter(description = "조회 날짜 (yyyy-MM-dd)", example = "2026-04-07") LocalDate date);

    @Operation(summary = "월간 출석 통계", description = "특정 학생의 한 달간 출석 현황(출석/지각/결석 횟수 및 비율)을 조회하네.")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<AttendanceSummaryResponse>> getMonthlyReport(
            @Parameter(description = "학생 ID") Long studentId,
            @Parameter(description = "년도") int year,
            @Parameter(description = "월") int month);
}