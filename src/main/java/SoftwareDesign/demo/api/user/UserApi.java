package SoftwareDesign.demo.api.user;

import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.api.user.dto.MeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "User", description = "사용자 정보 및 인증 관련 API")
public interface UserApi {

    @Operation(summary = "테스트용 토큰 발급", description = "비밀번호 검증 없이 이메일만으로 JWT 토큰을 즉시 발급하네. 테스트용이라네!")
    @ApiResponse(responseCode = "200", description = "토큰 발급 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> getTestToken(
            @Parameter(description = "등록된 유저의 이메일", example = "ryu@inu.ac.kr") String email);

    @Operation(summary = "내 정보 조회", description = "로그인한 유저의 상세 정보를 조회하네. 권한에 따라 학생/교사 정보가 포함된다네.")
    @ApiResponse(responseCode = "200", description = "정보 조회 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<MeResponse>> getMyInfo(
            @Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "학부모 자녀 검색", description = "로그인한 학부모의 자녀들을 검색")
    @ApiResponse(responseCode = "200", description = "정보 조회 성공")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<List<StudentResponse>>> getMyChildren(
            @Parameter(hidden = true) Authentication authentication);

}