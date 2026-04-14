package SoftwareDesign.demo.api.admin;

import SoftwareDesign.demo.api.admin.dto.ParentRegisterRequest;
import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Admin", description = "시스템 관리자용 API (유저 권한 및 역할 부여)")
public interface AdminApi {

    @Operation(summary = "유저 권한 수정", description = "특정 유저의 권한(ROLE_STUDENT, ROLE_TEACHER 등)을 직접 변경하네.")
    @ApiResponse(responseCode = "200", description = "권한 변경 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> updateUserRole(
            @Parameter(description = "대상 유저 ID") Long id,
            @RequestBody UserRoleUpdateRequest request);

    @Operation(summary = "학생 상세 정보 등록", description = "일반 유저를 학생으로 등록하고 상세 정보(학번, 반 등)를 입력하네.")
    @ApiResponse(responseCode = "201", description = "학생 등록 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> registerStudent(
            @Parameter(description = "대상 유저 ID") Long id,
            @RequestBody StudentCreateRequest request);

    @Operation(summary = "선생님 상세 정보 등록", description = "일반 유저를 선생님으로 등록하고 담당 과목을 지정하네.")
    @ApiResponse(responseCode = "201", description = "선생님 등록 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> registerTeacher(
            @Parameter(description = "대상 유저 ID") Long id,
            @Parameter(description = "담당 과목명", example = "수학") String subject);

    @Operation(summary = "학부모 상세 정보 등록", description = "학부모 등록")
    @ApiResponse(responseCode = "201", description = "학부모 등록 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> registerParent(
            @Parameter(description = "대상 유저 ID") Long id,
            @RequestBody ParentRegisterRequest request);


    @Operation(summary = "유저 상세 정보 등록", description = "테스트 용 유저 등록")
    @ApiResponse(responseCode = "201", description = "테스트 용 유저 등록 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> registerUser(
            @RequestBody UserCreateRequest request);


}
