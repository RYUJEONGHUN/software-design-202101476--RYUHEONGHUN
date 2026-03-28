package SoftwareDesign.demo.api.student;

import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentProfile(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 학생 못 찾을 때

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, new StudentResponse(student)));
    }
}