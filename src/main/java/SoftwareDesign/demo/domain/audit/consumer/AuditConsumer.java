package SoftwareDesign.demo.domain.audit.consumer;

import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
import SoftwareDesign.demo.api.notification.dto.ConsultationEvent;
import SoftwareDesign.demo.api.notification.dto.EventAction;
import SoftwareDesign.demo.api.notification.dto.FeedbackEvent;
import SoftwareDesign.demo.api.notification.dto.GradeEvent;
import SoftwareDesign.demo.domain.audit.entity.AuditLog;
import SoftwareDesign.demo.domain.audit.repository.AuditLogRepository;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_GRADE_QUEUE)
    public void handleGrade(GradeEvent event) {
        save(eventType("GRADE", event.getAction()), event.getStudentId(), RabbitMQConfig.GRADE_ROUTING_KEY,
                String.format("Grade %s. subject=%s, semester=%s",
                        actionText(event.getAction()), event.getSubjectName(), event.getSemester()),
                event);
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_ATTENDANCE_QUEUE)
    public void handleAttendance(AttendanceEvent event) {
        save(eventType("ATTENDANCE", event.getAction()), event.getStudentId(), RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
                String.format("Attendance %s. date=%s, status=%s",
                        actionText(event.getAction()), event.getDate(), event.getStatus()),
                event);
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_CONSULTATION_QUEUE)
    public void handleConsultation(ConsultationEvent event) {
        save(eventType("CONSULTATION", event.getAction()), event.getStudentId(), RabbitMQConfig.CONSULTATION_ROUTING_KEY,
                String.format("Consultation %s. date=%s, teacher=%s",
                        actionText(event.getAction()), event.getConsultationDate(), event.getTeacherName()),
                event);
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_FEEDBACK_QUEUE)
    public void handleFeedback(FeedbackEvent event) {
        save(eventType("FEEDBACK", event.getAction()), event.getStudentId(), RabbitMQConfig.FEEDBACK_ROUTING_KEY,
                String.format("Feedback %s. teacher=%s, date=%s",
                        actionText(event.getAction()), event.getTeacherName(), event.getCreatedDate()),
                event);
    }

    private void save(String eventType, Long studentId, String routingKey, String summary, Object event) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .studentId(studentId)
                .routingKey(routingKey)
                .summary(summary)
                .payload(toJson(event))
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log saved. eventType={}, studentId={}", eventType, studentId);
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit payload. eventClass={}", event.getClass().getSimpleName(), e);
            return "{}";
        }
    }

    private String eventType(String domain, EventAction action) {
        return domain + "_" + actionOrDefault(action);
    }

    private String actionText(EventAction action) {
        return actionOrDefault(action).name().toLowerCase();
    }

    private EventAction actionOrDefault(EventAction action) {
        return action == null ? EventAction.UPDATED : action;
    }
}
