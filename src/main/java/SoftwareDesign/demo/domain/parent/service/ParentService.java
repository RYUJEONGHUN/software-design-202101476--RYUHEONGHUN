package SoftwareDesign.demo.domain.parent.service;

import SoftwareDesign.demo.api.admin.dto.ParentRegisterRequest;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.parent.entity.Parent;
import SoftwareDesign.demo.domain.parent.repository.ParentRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void registerParent(Long userId, ParentRegisterRequest request) {
        // 기존 유저 조회 및 Role 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        user.updateRole(UserRole.PARENT);

        // 이미 학부모로 등록된 유저인지 체크하는 방어 로직
        if (parentRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_PARENT); // 이미 등록된 학생
        }

        // Parent 엔티티 생성
        Parent parent = parentRepository.save(Parent.builder().user(user).build());

        // 자녀들 한꺼번에 조회 (N+1 방지)
        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(request.getStudentIds());

            // 찾은 학생 수와 요청받은 ID 수가 다르면 예외 처리
            if (students.size() != request.getStudentIds().size()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            // 자녀 연결
            for (Student student : students) {
                boolean isAlreadyAdded = parent.getChildren().stream()
                        .anyMatch(pc -> pc.getStudent().getId().equals(student.getId()));

                if (!isAlreadyAdded) {
                    parent.addChild(student);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getMyChildrenByEmail(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Parent parent = parentRepository.findByIdWithChildren(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        return parent.getChildren().stream()
                .map(pc -> new StudentResponse(pc.getStudent()))
                .collect(Collectors.toList());
    }

}
