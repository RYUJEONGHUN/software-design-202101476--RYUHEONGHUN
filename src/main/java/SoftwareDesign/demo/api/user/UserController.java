package SoftwareDesign.demo.api.user;

import SoftwareDesign.demo.api.user.dto.MeResponse;
import SoftwareDesign.demo.api.user.dto.UserResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import SoftwareDesign.demo.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    @GetMapping("/test-token")
    public ResponseEntity<ApiResponse<String>> getTestToken(@RequestParam String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // JwtTokenProvider를 사용해 바로 토큰 생성 (비밀번호 검증 없이!)
        String token = tokenProvider.createAccessToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, token));
    }


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> getMyInfo(Authentication authentication) {
        String email = authentication.getName(); // JWT에서 꺼낸 이메일

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없구먼!"));

        MeResponse.MeResponseBuilder builder = MeResponse.builder()
                .email(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name());

        // 권한에 따라 상세 정보 추가 조회
        if (user.getRole() == UserRole.STUDENT) {
            studentRepository.findById(user.getId()).ifPresent(s ->
                    builder.student(MeResponse.StudentDetail.builder()
                            .studentNumber(s.getStudentNumber())
                            .grade(s.getGrade())
                            .classNum(s.getClassNum())
                            .number(s.getNumber()).build())
            );
        } else if (user.getRole() == UserRole.TEACHER) {
            teacherRepository.findById(user.getId()).ifPresent(t ->
                    builder.teacher(MeResponse.TeacherDetail.builder()
                            .subject(t.getSubject())
                            .teacherIdNum(t.getTeacherIdNum()).build())
            );
        }

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, builder.build()));
    }
}