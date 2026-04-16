package SoftwareDesign.demo.domain.notification.consumer;

import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
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

    @RabbitListener(queues = RabbitMQConfig.ATTENDANCE_QUEUE)
    public void handleAttendance(AttendanceEvent event) {
        try {
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
        } catch (Exception e) {
            log.error(">>>> [Consumer] 처리 중 에러 발생!", e); // 👈 에러가 터지는지!
        }
    }
}