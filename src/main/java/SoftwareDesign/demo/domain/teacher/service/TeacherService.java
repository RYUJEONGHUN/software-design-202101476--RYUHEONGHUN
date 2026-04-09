package SoftwareDesign.demo.domain.teacher.service;

import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import SoftwareDesign.demo.domain.subject.repository.SubjectRepository;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void registerTeacher(Long userId, String subjectName) { // 또는 subjectId
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 과목 조회
        Subject subject = subjectRepository.findByName(subjectName)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBJECT_NOT_FOUND));

        // 이미 선생으로 등록된 유저인지 체크하는 방어 로직
        if (teacherRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_TEACHER); // 이미 등록된 선생
        }
        // 3. 권한 변경
        user.updateRole(UserRole.TEACHER);

        // 4. 교사 엔티티 생성 및 저장 (Subject 객체를 직접 연결!)
        Teacher teacher = Teacher.builder()
                .user(user)
                .subject(subject) // 이제 String이 아니라 Subject 객체가 들어간다네!
                .teacherIdNum("T-" + System.currentTimeMillis())
                .build();

        teacherRepository.save(teacher);
    }
}
