package SoftwareDesign.demo.api.grade;

import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            @RequestBody GradeCreateRequest request);

    @Operation(summary = "레이더 차트용 성적 조회", description = "특정 학기의 성적 데이터를 차트 형태로 가져오네.")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<GradeChartResponse>> getMyGradeChart(
            @Parameter(description = "학생 ID") Long studentId,
            @Parameter(description = "조회할 학기") String semester,
            Authentication authentication);
}
