package SoftwareDesign.demo.domain.notification.consumer;

import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
import SoftwareDesign.demo.api.notification.dto.ConsultationEvent;
import SoftwareDesign.demo.api.notification.dto.FeedbackEvent;
import SoftwareDesign.demo.api.notification.dto.GradeEvent;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import SoftwareDesign.demo.domain.notification.entity.NotificationType;
import SoftwareDesign.demo.domain.notification.service.NotificationService;
import SoftwareDesign.demo.domain.parent.repository.ParentRepository;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;

    // 출석 알림 처리
    @RabbitListener(queues = RabbitMQConfig.ATTENDANCE_QUEUE)
    public void handleAttendance(AttendanceEvent event) {
        // 학생 본인에게 알림
        notificationService.send(
                event.getStudentId(),
                NotificationType.ATTENDANCE_UPDATED,
                String.format("오늘의 출결 상태가 [%s]로 기록되었습니다.", event.getStatus())
        );

        // 학부모님 찾아서 조건부 알림
        if (event.getStatus() == AttendanceStatus.ABSENT || event.getStatus() == AttendanceStatus.TARDY) {
            parentRepository.findByStudentId(event.getStudentId()).ifPresent(parent -> {
                notificationService.send(
                        parent.getUser().getId(),
                        NotificationType.ATTENDANCE_UPDATED,
                        String.format("%s 학생이 오늘 %s 처리되었습니다.", event.getStudentName(), event.getStatus())
                );
            });
        }
    }
    // 성적 등록 처리
    @RabbitListener(queues = RabbitMQConfig.GRADE_QUEUE)
    public void handleGrade(GradeEvent event) {
        // 학생에게
        notificationService.send(
                event.getStudentId(),
                NotificationType.GRADE_UPDATED,
                String.format("[%s] 성적이 등록되었습니다: %d점", event.getSubjectName(), event.getScore())
        );

        // 모든 학부모에게
        parentRepository.findByStudentId(event.getStudentId()).ifPresent(parent -> {
            notificationService.send(
                    parent.getUser().getId(),
                    NotificationType.GRADE_UPDATED,
                    String.format("자녀의 [%s] 성적이 등록되었습니다.", event.getSubjectName()));
        });
    }

    // 피드백 알림 처리
    @RabbitListener(queues = RabbitMQConfig.FEEDBACK_QUEUE)
    public void handleFeedback(FeedbackEvent event) {
        notificationService.send(
                event.getStudentId(),
                NotificationType.FEEDBACK_CREATED,
                String.format("%s 선생님의 새로운 피드백이 도착했습니다.", event.getTeacherName()));
    }

    @RabbitListener(queues = RabbitMQConfig.CONSULTATION_QUEUE)
    public void handleConsultation(ConsultationEvent event) {
        // 학생에게 알림
        notificationService.send(
                event.getStudentId(),
                NotificationType.CONSULTATION_UPDATED,
                String.format("%s 선생님과의 상담 내역이 업데이트되었습니다.", event.getTeacherName())
        );

        // 학부모에게 알림
        parentRepository.findByStudentId(event.getStudentId()).ifPresent(parent -> {
            notificationService.send(
                    parent.getUser().getId(),
                    NotificationType.CONSULTATION_UPDATED,
                    String.format("%s 학생의 상담 일지가 작성되었습니다. 내용을 확인해 주세요.", event.getStudentName())
            );
        });
    }
}