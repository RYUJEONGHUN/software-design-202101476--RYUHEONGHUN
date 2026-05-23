package SoftwareDesign.demo.domain.analytics.consumer;

import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
import SoftwareDesign.demo.api.notification.dto.ConsultationEvent;
import SoftwareDesign.demo.api.notification.dto.FeedbackEvent;
import SoftwareDesign.demo.api.notification.dto.GradeEvent;
import SoftwareDesign.demo.domain.analytics.service.AnalyticsService;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_GRADE_QUEUE)
    public void handleGrade(GradeEvent event) {
        refresh(event.getSemester(), "grade");
    }

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_ATTENDANCE_QUEUE)
    public void handleAttendance(AttendanceEvent event) {
        refresh(toSemester(event.getDate()), "attendance");
    }

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_CONSULTATION_QUEUE)
    public void handleConsultation(ConsultationEvent event) {
        refresh(toSemester(LocalDate.parse(event.getConsultationDate())), "consultation");
    }

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_FEEDBACK_QUEUE)
    public void handleFeedback(FeedbackEvent event) {
        refresh(toSemester(event.getCreatedDate()), "feedback");
    }

    private void refresh(String semester, String eventType) {
        int rowCount = analyticsService.refreshClassSubjectAnalytics(semester);
        log.info("OLAP analytics refreshed by {} event. semester={}, rows={}", eventType, semester, rowCount);
    }

    private String toSemester(LocalDate date) {
        int term = date.getMonthValue() >= 3 && date.getMonthValue() <= 8 ? 1 : 2;
        int year = date.getMonthValue() <= 2 ? date.getYear() - 1 : date.getYear();
        return year + "-" + term;
    }
}
