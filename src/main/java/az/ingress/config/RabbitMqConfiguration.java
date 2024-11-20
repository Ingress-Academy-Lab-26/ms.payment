package az.ingress.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {
    private final String paymentQueue;
    private final String refundQueue;
    private final String paymentDLQ;
    private final String refundDLQ;

    public RabbitMqConfiguration(
            @Value("${rabbitmq.payment.queue}") String paymentQueue,
            @Value("${rabbitmq.payment.dlq}") String paymentDLQ,
            @Value("${rabbitmq.refund.queue}") String refundQueue,
            @Value("${rabbitmq.refund.dlq}") String refundDLQ) {

        this.paymentQueue = paymentQueue;
        this.paymentDLQ = paymentDLQ;
        this.refundQueue = refundQueue;
        this.refundDLQ = refundDLQ;
    }

    @Bean
    DirectExchange paymentExchange() {
        return new DirectExchange(paymentQueue + "_EXCHANGE");
    }

    @Bean
    DirectExchange refundExchange() {
        return new DirectExchange(refundQueue + "_EXCHANGE");
    }

    @Bean
    Queue paymentQueue() {
        return QueueBuilder.durable(paymentQueue)
                .withArgument("x-dead-letter-exchange", paymentDLQ + "_EXCHANGE")
                .withArgument("x-dead-letter-routing-key", paymentDLQ + "_KEY")
                .build();
    }

    @Bean
    Queue refundQueue() {
        return QueueBuilder.durable(refundQueue)
                .withArgument("x-dead-letter-exchange", refundDLQ + "_EXCHANGE")
                .withArgument("x-dead-letter-routing-key", refundDLQ + "_KEY")
                .build();
    }

    @Bean
    Binding paymentQueueBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(paymentExchange())
                .with(paymentQueue + "_KEY");
    }

    @Bean
    Binding refundQueueBinding() {
        return BindingBuilder.bind(refundQueue())
                .to(refundExchange())
                .with(refundQueue + "_KEY");
    }
}