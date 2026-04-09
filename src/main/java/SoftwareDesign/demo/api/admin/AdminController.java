package SoftwareDesign.demo.api.admin;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserRoleUpdateRequest;
import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.student.service.StudentService;
import SoftwareDesign.demo.domain.teacher.service.TeacherService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import SoftwareDesign.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController implements AdminApi{

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final UserService userService;


    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<String>> updateUserRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateRequest request) {

        userService.updateUserRole(id,request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS,
                "유저 권한이 " + request.getRole() + "(으)로 변경되었습니다."));
    }


    @PostMapping("/users/{id}/register-student")
    public ResponseEntity<ApiResponse<String>> registerStudent(
            @PathVariable Long id,
            @RequestBody StudentCreateRequest request) {

        studentService.registerStudent(id, request);

        return ResponseEntity.status(SuccessCode.CREATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.success(SuccessCode.CREATE_SUCCESS, "학생 등록 및 권한 변경 완료"));
    }

    @PostMapping("/users/{id}/register-teacher")
    public ResponseEntity<ApiResponse<String>> registerTeacher(
            @PathVariable Long id,
            @RequestParam String subject) {

        teacherService.registerTeacher(id, subject);
        return ResponseEntity.status(SuccessCode.CREATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.success(SuccessCode.CREATE_SUCCESS, "선생님 등록 완료"));
    }

    //테스트 용 유저 등록
    @Transactional
    @PostMapping("/users/register-user")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @RequestBody UserCreateRequest request) {

        userService.registerUser(request);

        return ResponseEntity.status(SuccessCode.CREATE_SUCCESS.getHttpStatus())
                .body(ApiResponse.success(SuccessCode.CREATE_SUCCESS, "유저 등록 완료"));

    }
}