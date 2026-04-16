package SoftwareDesign.demo.domain.feedback.service;

import SoftwareDesign.demo.api.feedback.dto.FeedbackCreateRequest;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackUpdateRequest;
import SoftwareDesign.demo.api.notification.dto.FeedbackEvent;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import SoftwareDesign.demo.domain.feedback.repository.FeedbackRepository;
import SoftwareDesign.demo.domain.parent.entity.Parent;
import SoftwareDesign.demo.domain.parent.repository.ParentRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public void createFeedback(String email, FeedbackCreateRequest request) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        // 대상 학생 조회
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 피드백 생성 및 저장
        Feedback feedback = Feedback.builder()
                .category(request.getCategory())
                .content(request.getContent())
                .visibleToParent(request.isVisibleToParent()) // 옵션 반영
                .teacher(teacher)
                .student(student)
                .build();

        feedbackRepository.save(feedback);

        // 학생에게 알림 발송
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.FEEDBACK_ROUTING_KEY,
                FeedbackEvent.from(feedback)
        );
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForParent(String email, Long studentId) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Parent parent = parentRepository.findByIdWithChildren(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        boolean isMyChild = parent.getChildren().stream()
                .anyMatch(parentChild -> parentChild.getStudent().getId().equals(studentId));

        if(!isMyChild){
            throw new CustomException(ErrorCode.NOT_YOUR_CHILD);
        }
        // 공개된 피드백만 조회
        return feedbackRepository.findByStudentIdAndIsVisibleToParentTrue(studentId)
                .stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    // 해당학생의 모든 피드백 가져오기
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForStaff(Long studentId) {
        return feedbackRepository.findAllByStudentId(studentId)
                .stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateFeedback(String email, Long feedbackId, FeedbackUpdateRequest request) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        //자기가 작성한 피드백인지 확인(선생)
        if (!feedback.getTeacher().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_YOUR_FEEDBACK);
        }

        // 더티 체킹으로 반영
        feedback.updateContent(request.getContent(), request.getCategory(), request.isVisibleToParent());
    }
}
