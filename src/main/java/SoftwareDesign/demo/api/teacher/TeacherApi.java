package SoftwareDesign.demo.api.teacher;

import SoftwareDesign.demo.api.teacher.dto.ScheduledConsultationDto;
import SoftwareDesign.demo.api.teacher.dto.TeacherDashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "Teacher Dashboard", description = "선생님 대시보드 및 통계 관련 API")
public interface TeacherApi {

    @Operation(summary = "선생님 대시보드 메인 정보 조회", description = "로그인한 선생님의 성함, 담당 과목, 오늘 미출석 인원 및 예약 상담 건수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대시보드 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "선생님 정보를 찾을 수 없음")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<TeacherDashboardResponse>> getTeacherDashboard(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "예약된 상담 목록 조회", description = "오늘 날짜 이후로 예정된 상담 일정 목록을 시간순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상담 목록 조회 성공")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<List<ScheduledConsultationDto>>> getScheduledConsultations(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "개별 학생 출석률 조회", description = "특정 학생의 누적 출석률을 조회합니다. (Redis 캐시 활용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출석률 조회 성공"),
            @ApiResponse(responseCode = "404", description = "학생 정보를 찾을 수 없음")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<Double>> getStudentAttendanceRate(
            @Parameter(description = "조회할 학생의 고유 ID", example = "1") Long studentId
    );
}