package SoftwareDesign.demo.domain.teacher.service;

import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
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

    @Transactional
    public void registerTeacher(Long userId, String subject) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 2. 권한 변경
        user.updateRole(UserRole.TEACHER);

        // 2. 교사 상세 정보 생성 및 저장
        Teacher teacher = Teacher.builder()
                .user(user)
                .subject(subject)
                .teacherIdNum("T-" + System.currentTimeMillis()) // 임시 번호 생성 예시
                .build();

        teacherRepository.save(teacher);
    }
}