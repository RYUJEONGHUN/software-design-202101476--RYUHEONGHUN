package SoftwareDesign.demo.domain.student.service;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Transactional //
    public void registerStudent(Long userId, StudentCreateRequest request) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. 유저 권한 변경 (STUDENT로!)
        user.updateRole(UserRole.STUDENT);
        // Dirty Checking 덕분에 따로 save를 안 해도 트랜잭션이 끝날 때 반영된다네.

        // 3. 학생 상세 정보 저장
        Student student = Student.builder()
                .user(user)
                .studentNumber(request.getStudentNumber())
                .grade(request.getGrade())
                .classNum(request.getClassNum())
                .number(request.getNumber())
                .build();

        studentRepository.save(student);

        // 만약 여기서 에러가 나면? 권한 변경도 자동으로 취소(Rollback)된다네!
    }
}