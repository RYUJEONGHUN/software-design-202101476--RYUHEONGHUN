package SoftwareDesign.demo.api.student;


import SoftwareDesign.demo.api.student.dto.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

}
