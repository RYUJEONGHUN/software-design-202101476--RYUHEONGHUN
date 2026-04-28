package SoftwareDesign.demo.domain.consultation.service;

import SoftwareDesign.demo.api.consultation.dto.ConsultationRequest;
import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.api.notification.dto.ConsultationEvent;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.consultation.repository.ConsultationRepository;
import SoftwareDesign.demo.domain.notification.entity.NotificationType;
import SoftwareDesign.demo.domain.notification.service.NotificationService;
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
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;


    // 상담 기록 저장
    @Transactional
    public void createConsultation(ConsultationRequest request, String userEmail) {
        User user = userRepository.findByUsername(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        Consultation consultation = Consultation.builder()
                .teacher(teacher)
                .student(student)
                .consultationDate(request.getConsultationDate())
                .content(request.getContent())
                .nextPlanDate(request.getNextPlanDate())
                .build();

        consultationRepository.save(consultation);

        // 알림 쏘기
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.CONSULTATION_ROUTING_KEY,
                ConsultationEvent.from(consultation)
        );

    }

    // 조건별 상담 내역 검색
    public List<ConsultationResponse> searchConsultations(ConsultationSearchCondition condition) {
        return consultationRepository.search(condition).stream()
                .map(ConsultationResponse::new)
                .collect(Collectors.toList());
    }
}


