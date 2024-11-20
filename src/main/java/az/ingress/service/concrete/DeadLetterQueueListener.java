package az.ingress.service.concrete;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.service.abstraction.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadLetterQueueListener {

    private final PaymentProcessor paymentProcessorHandler;

    @RabbitListener(queues = "dlq-paymentQueue")
    @Retryable(
            maxAttemptsExpression = "${spring.rabbitmq.listener.simple.retry.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${spring.rabbitmq.listener.simple.retry.initial-interval}",
                    multiplierExpression = "${spring.rabbitmq.listener.simple.retry.multiplier}",
                    maxDelayExpression = "${spring.rabbitmq.listener.simple.retry.max-interval}"
            )
    )
    public void processDeadLetterQueueMessage(PaymentEntity paymentEntity) {
        log.info("ActionLog.info Message received in Dead Letter Queue for paymentId: {}", paymentEntity.getId());

        try {
            paymentProcessorHandler.processPayment("accessToken", paymentEntity);
            log.info("ActionLog.info Successfully reprocessed DLQ message for paymentId: {}", paymentEntity.getId());
        } catch (Exception e) {
            log.error("ActionLog.erro Error processing DLQ message for paymentId: {}, Error: {}", paymentEntity.getId(), e.getMessage());
        }
    }
}

