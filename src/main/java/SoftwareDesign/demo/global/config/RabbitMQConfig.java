package SoftwareDesign.demo.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 출석용
    public static final String ATTENDANCE_QUEUE = "attendance.queue";
    public static final String ATTENDANCE_ROUTING_KEY = "attendance.key";

    // 성적용
    public static final String GRADE_QUEUE = "grade.queue";
    public static final String GRADE_ROUTING_KEY = "grade.key";

    // 피드백용
    public static final String FEEDBACK_QUEUE = "feedback.queue";
    public static final String FEEDBACK_ROUTING_KEY = "feedback.key";

    public static final String COMMON_EXCHANGE = "common.exchange";


    // 상담용
    public static final String CONSULTATION_QUEUE = "consultation.queue";
    public static final String CONSULTATION_ROUTING_KEY = "consultation.key";


    @Bean Queue gradeQueue() {
        return new Queue(GRADE_QUEUE, true);
    }
    @Bean Queue feedbackQueue() {
        return new Queue(FEEDBACK_QUEUE, true);
    }
    @Bean
    public Queue attendanceQueue() {
        return new Queue(ATTENDANCE_QUEUE, true);
    }
    @Bean
    public Queue consultationQueue() {
        return new Queue(CONSULTATION_QUEUE, true);
    }

    @Bean
    public Binding gradeBinding(Queue gradeQueue, DirectExchange commonExchange) {
        return BindingBuilder.bind(gradeQueue)
                .to(commonExchange)
                .with(GRADE_ROUTING_KEY);
    }

    @Bean
    public Binding feedbackBinding(Queue feedbackQueue, DirectExchange commonExchange) {
        return BindingBuilder.bind(feedbackQueue)
                .to(commonExchange)
                .with(FEEDBACK_ROUTING_KEY);
    }

    @Bean
    public Binding attendanceBinding(Queue attendanceQueue, DirectExchange commonExchange) {
        return BindingBuilder.bind(attendanceQueue)
                .to(commonExchange)
                .with(ATTENDANCE_ROUTING_KEY);
    }

    @Bean
    public Binding consultationBinding(Queue consultationQueue, DirectExchange commonExchange) {
        return BindingBuilder.bind(consultationQueue)
                .to(commonExchange)
                .with(CONSULTATION_ROUTING_KEY);
    }

    @Bean
    public DirectExchange commonExchange() {
        return new DirectExchange(COMMON_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}