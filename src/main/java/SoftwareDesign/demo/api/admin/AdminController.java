package SoftwareDesign.demo.api.admin;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserRoleUpdateRequest;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.student.service.StudentService;
import SoftwareDesign.demo.domain.teacher.service.TeacherService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;


    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateRequest request) {

        return userRepository.findById(id)
                .map(user -> {
                    // 유저 엔티티에 role 변경 메서드가 필요하겠구먼!
                    user.updateRole(request.getRole());
                    userRepository.save(user);
                    return ResponseEntity.ok("유저 권한이 " + request.getRole() + "(으)로 변경되었습니다.");
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/users/{id}/register-student")
    public ResponseEntity<String> registerStudent(
            @PathVariable Long id,
            @RequestBody StudentCreateRequest request) {

        studentService.registerStudent(id, request);

        return ResponseEntity.ok("학생 등록 및 권한 변경이 완료되었습니다.");
    }

    @PostMapping("/users/{id}/register-teacher")
    public ResponseEntity<String> registerTeacher(
            @PathVariable Long id,
            @RequestParam String subject) {

        teacherService.registerTeacher(id, subject);
        return ResponseEntity.ok("선생님 등록이 완료되었습니다.");
    }
}