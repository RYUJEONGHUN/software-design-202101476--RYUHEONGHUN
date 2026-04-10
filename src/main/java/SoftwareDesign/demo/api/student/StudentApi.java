package SoftwareDesign.demo.api.student;


import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Student", description = "학생 관리 API")
public interface StudentApi {
    @Operation(summary = "특정 학생 조회", description = "학생 프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "유저 없음"),
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<StudentResponse>> getStudentProfile(
            @PathVariable Long id,
            Authentication authentication
    );

    @Operation(summary = "학생 통합 검색 및 리스트 조회", description = "학년, 반, 이름 조건을 활용한 학생 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (선생님/관리자만 가능)")
    })
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<Page<StudentSummaryResponse>>> search(
            @Parameter(description = "검색 조건 (학년, 반, 이름)") StudentSearchCondition condition,
            Pageable pageable
    );

}
