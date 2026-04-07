package SoftwareDesign.demo.api.student;

import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.student.service.StudentService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController implements StudentApi{

    private final UserRepository userRepository;
    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentProfile(@PathVariable Long id, Authentication authentication) {
        // 1. 현재 로그인한 유저 정보 가져오기
        String email = authentication.getName();
        User loginUser = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 보안 체크
        if (loginUser.getRole() == UserRole.STUDENT) {
            // 학생이라면 본인 ID와 요청 ID가 일치해야 함
            if (!loginUser.getId().equals(id)) {
                throw new CustomException(ErrorCode.FORBIDDEN); // 남의 프로필은 못 본다.
            }
        }
        // 3. 프로필 조회 및 반환
        StudentResponse response = studentService.getStudentProfile(id);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }
}
