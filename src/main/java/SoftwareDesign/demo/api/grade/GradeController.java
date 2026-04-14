package SoftwareDesign.demo.api.grade;

import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeCreateRequest;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.grade.service.GradeService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController implements GradeApi{

    private final GradeService gradeService;
    private final UserRepository userRepository;

    // 성적 입력 (교사 권한)
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')") // 시큐리티 설정이 되어있다면!
    public ResponseEntity<ApiResponse<String>> registerGrade(
            @RequestBody GradeCreateRequest request,
            Authentication authentication) {

        gradeService.registerGrade(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GRADE_REGISTER_SUCCESS, "성적이 등록되었네"));
    }

    // 레이더 차트용 성적 조회 (본인 혹은 교사)
    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<GradeChartResponse>> getMyGradeChart(
            @RequestParam Long studentId,
            @RequestParam String semester,
            Authentication authentication) {

        // 1. 토큰에서 로그인한 유저의 이메일(혹은 ID) 꺼내기
        String loginUserEmail = authentication.getName();
        User user = userRepository.findByUsername(loginUserEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 보안 대조
        if (user.getRole() == UserRole.STUDENT) {
            // 로그인한 유저의 식별자와 요청한 studentId가 다르면 에러!
            if (!user.getId().equals(studentId)) {
                throw new CustomException(ErrorCode.FORBIDDEN); // 남의 성적은 못 본다
            }
        }else if (user.getRole() != UserRole.TEACHER && user.getRole() != UserRole.ADMIN) {
            // 학생도 아니고 선생님/관리자도 아니라면?
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 3. 검증 통과 후 로직 실행
        GradeChartResponse response = gradeService.getGradeChart(studentId, semester);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }
}
