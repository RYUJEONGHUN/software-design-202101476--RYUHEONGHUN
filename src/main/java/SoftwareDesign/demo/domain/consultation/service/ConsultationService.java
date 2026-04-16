package SoftwareDesign.demo.domain.consultation.service;

import SoftwareDesign.demo.api.consultation.dto.ConsultationRequest;
import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
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
import lombok.RequiredArgsConstructor;
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
    private final NotificationService notificationService;
    private final ParentRepository parentRepository;

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
                .nextPlan(request.getNextPlan())
                .build();

        consultationRepository.save(consultation);

        // 학생에게 상담 등록 알림 발송
        notificationService.send(
                student.getUser().getId(),
                NotificationType.CONSULTATION_UPDATED,
                "새로운 상담내역(" + request.getConsultationDate() + ")이 등록되었습니다. 확인해 보세요!"
        );

        // 학부모에게 알림 발송
        parentRepository.findByStudentId(student.getId()).ifPresent(parent -> {
            notificationService.send(
                    parent.getUser().getId(),
                    NotificationType.CONSULTATION_UPDATED,
                    student.getUser().getName() + " 학생의 새로운 상담 내역이 등록되었습니다."
            );
        });

    }

    // 조건별 상담 내역 검색
    public List<ConsultationResponse> searchConsultations(ConsultationSearchCondition condition) {
        return consultationRepository.search(condition).stream()
                .map(ConsultationResponse::new)
                .collect(Collectors.toList());
    }
}


