package SoftwareDesign.demo.domain.teacher.service;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 1. 권한 변경 (Dirty Checking으로 자동 업데이트)
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