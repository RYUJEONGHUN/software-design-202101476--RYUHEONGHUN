package SoftwareDesign.demo.domain.student.service;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 학생으로 등록된 유저인지 체크하는 방어 로직이 있으면 더 좋네!
        if (studentRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT); // "이미 등록된 학생이라네!" 라는 의미
        }

        user.updateRole(UserRole.STUDENT);

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

    @Transactional
    public StudentResponse getStudentProfile(Long userid){

        Student student = studentRepository.findById(userid)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        return new StudentResponse(student);
    }

}