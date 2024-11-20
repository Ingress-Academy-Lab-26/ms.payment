package az.ingress.config;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable("paymentQueue")
                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "dlq-paymentQueue")
                .build();
    }

    @Bean
    public Queue dlqPaymentQueue() {
        return new Queue("dlq-paymentQueue", true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("exchange-name");
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue()).to(exchange()).with("paymentQueue");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqPaymentQueue()).to(exchange()).with("dlq-paymentQueue");
    }

}

