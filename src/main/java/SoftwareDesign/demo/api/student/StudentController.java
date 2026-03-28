package SoftwareDesign.demo.api.student;

import SoftwareDesign.demo.api.student.dto.StudentResponse;
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
    public ResponseEntity<StudentResponse> getStudentProfile(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(new StudentResponse(student)))
                .orElse(ResponseEntity.notFound().build());
    }
}