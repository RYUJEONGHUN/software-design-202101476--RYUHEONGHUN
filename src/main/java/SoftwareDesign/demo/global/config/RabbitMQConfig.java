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

    public static final String ATTENDANCE_QUEUE = "attendance.queue";
    public static final String ATTENDANCE_EXCHANGE = "attendance.exchange";
    public static final String ATTENDANCE_ROUTING_KEY = "attendance.key";

    @Bean
    public Queue attendanceQueue() {
        return new Queue(ATTENDANCE_QUEUE, true);
    }

    @Bean
    public DirectExchange attendanceExchange() {
        return new DirectExchange(ATTENDANCE_EXCHANGE);
    }

    @Bean
    public Binding attendanceBinding(Queue attendanceQueue, DirectExchange attendanceExchange) {
        return BindingBuilder.bind(attendanceQueue)
                .to(attendanceExchange)
                .with(ATTENDANCE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}