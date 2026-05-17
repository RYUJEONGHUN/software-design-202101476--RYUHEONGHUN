package SoftwareDesign.demo.api.grade;

import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeCreateRequest;
import SoftwareDesign.demo.api.grade.dto.GradeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Grade", description = "성적 관리 API")
public interface GradeApi {

    @Operation(summary = "성적 등록", description = "선생님이 학생의 성적을 입력.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성적 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력값"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> registerGrade(
            @RequestBody GradeCreateRequest request, Authentication authentication);

    @Operation(summary = "레이더 차트용 성적 조회", description = "특정 학기의 성적(내 점수,반 평균,전체 평균) 데이터를 차트 형태로 가져오네.")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<GradeChartResponse>> getMyGradeChart(
            @Parameter(description = "학생 ID") Long studentId,
            @Parameter(description = "조회할 학기") String semester,
            Authentication authentication);

    @Operation(summary = "성적 수정", description = "교사가 본인 과목의 기존 성적 점수를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성적 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 점수"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> updateGrade(
            @PathVariable Long gradeId,
            @RequestBody GradeUpdateRequest request,
            Authentication authentication);
}
