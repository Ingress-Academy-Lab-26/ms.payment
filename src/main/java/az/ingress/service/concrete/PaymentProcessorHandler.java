package az.ingress.service.concrete;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.dao.repository.PaymentRepository;
import az.ingress.exception.InsufficientBalanceException;
import az.ingress.exception.PaymentProcessingException;
import az.ingress.service.abstraction.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static az.ingress.exception.ErrorMessage.PAYMENT_PROCESSING_EXCEPTION;
import static az.ingress.model.enums.PaymentStatus.COMPLETED;
import static az.ingress.model.enums.PaymentStatus.FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessorHandler implements PaymentProcessor {
    PaymentRepository paymentRepository;
    CardService cardService;
    private final AmqpTemplate amqpTemplate;


    @RabbitListener(queues = "paymentQueue")
    @Override
    public void processPayment(String accessToken, PaymentEntity paymentEntity) {
        try {
            boolean paymentSuccessful = cardService.processCardPayment(accessToken, paymentEntity.getAmount());
            if (!paymentSuccessful) {
                throw new InsufficientBalanceException("User does not have enough balance.", 422);
            }
            log.info("ActionLog.info Procession payment for paymentId:{} successful.", paymentEntity.getId());

            paymentEntity.setStatus(COMPLETED);
            paymentRepository.save(paymentEntity);
            log.info("ActionLog.info Payment status successfully changed for paymentId:{}", paymentEntity.getId());
        }
        catch (Exception e) {
            log.error("ActionLog.error Error processing payment for paymentId: {}, Error: {}", paymentEntity.getId(), e.getMessage());
            paymentEntity.setStatus(FAILED);
            paymentRepository.save(paymentEntity);

            amqpTemplate.convertAndSend("dlq-exchange", "dlq-paymentQueue", paymentEntity);
            log.info("Message sent to DLQ for paymentId: {}", paymentEntity.getId());
            throw new PaymentProcessingException(PAYMENT_PROCESSING_EXCEPTION.getMessage(), 422);
        }
    }

    @RabbitListener(queues = "refundQueue")
    @Override
    public void processRefund(String accessToken, PaymentEntity paymentEntity) {
        try {
            cardService.refundPayment(accessToken, paymentEntity.getAmount());
            log.info("ActionLog.info Message: Refund processed for payment Id: {}", paymentEntity.getId());
        } catch (Exception e) {
            amqpTemplate.convertAndSend("dlq-exchange", "dlq-refundQueue", paymentEntity);
            log.info("Message sent to DLQ for refund of paymentId: {}", paymentEntity.getId());
            //log.error("ActionLog.error Error processing refund for payment Id: {}, Error: {}", paymentEntity.getId(), e.getMessage());
        }
    }
}
