package SoftwareDesign.demo.api.student;

import SoftwareDesign.demo.api.student.dto.StudentDetailResponse;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.student.service.StudentService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController implements StudentApi{

    private final UserRepository userRepository;
    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentProfile(@PathVariable Long id, Authentication authentication) {
        //  현재 로그인한 유저 정보 가져오기
        String email = authentication.getName();
        User loginUser = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //  보안 체크
        if (loginUser.getRole() == UserRole.STUDENT) {
            // 학생이라면 본인 ID와 요청 ID가 일치해야 함
            if (!loginUser.getId().equals(id)) {
                throw new CustomException(ErrorCode.FORBIDDEN); // 남의 프로필은 못 본다.
            }
        }
        //  프로필 조회 및 반환
        StudentDetailResponse response = studentService.getStudentProfile(id);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<StudentSummaryResponse>>> search(
            StudentSearchCondition condition, Pageable pageable) {

        Page<StudentSummaryResponse> result = studentService.searchStudents(condition,pageable);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, result));
    }
}
