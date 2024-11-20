package az.ingress.service.concrete;

import az.ingress.aop.annotation.Log;
import az.ingress.dao.entity.PaymentEntity;
import az.ingress.dao.repository.PaymentRepository;
import az.ingress.exception.PaymentProcessingException;
import az.ingress.service.abstraction.PaymentGateway;
import az.ingress.service.abstraction.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static az.ingress.exception.ErrorMessage.PAYMENT_PROCESSING_EXCEPTION;
import static az.ingress.exception.ErrorMessage.REFUND_EXCEPTION;
import static az.ingress.model.enums.PaymentStatus.*;
import static az.ingress.model.mapper.PaymentMapper.*;

@Service
@Log
@RequiredArgsConstructor
public class PaymentProcessorHandler implements PaymentProcessor {
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;


    @Value("${rabbitmq.payment.queue}")
    private String paymentQueue;

    @Value("${rabbitmq.refund.queue}")
    private String refundQueue;

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 1000,
                    maxDelay = 10000,
                    multiplier = 2.0
            )
    )
    @RabbitListener(queues = "${rabbitmq.payment.queue}")
    @Override
    public void processPayment(PaymentEntity paymentEntity) {
        boolean isAlreadyProcessed = paymentRepository.existsByIdempotencyKey(paymentEntity.getIdempotencyKey());
        if (isAlreadyProcessed) {
            // log.info("Payment with idempotencyKey {} is already processed. Skipping...", paymentEntity.getIdempotencyKey());
            return;
        }
        try {
            paymentGateway.processPayment(PAYMENT_MAPPER.buildPaymentDto(paymentEntity));
            paymentEntity.setStatus(COMPLETED);
            paymentRepository.save(paymentEntity);
        } catch (Exception e) {
            paymentEntity.setStatus(FAILED);
            paymentRepository.save(paymentEntity);
            throw new PaymentProcessingException(PAYMENT_PROCESSING_EXCEPTION.getMessage(), 422);
        }
    }

    @RabbitListener(queues = "${rabbitmq.refund.queue}")
    @Override
    public void processRefund(PaymentEntity paymentEntity) {
        try {
            paymentGateway.processRefund(PAYMENT_MAPPER.buildPaymentDto(paymentEntity));
            paymentEntity.setStatus(REFUNDED);
            paymentRepository.save(paymentEntity);
        } catch (Exception e) {
            paymentEntity.setStatus(FAILED);
            paymentRepository.save(paymentEntity);
            throw new PaymentProcessingException(REFUND_EXCEPTION.getMessage(), 422);
        }
    }
}
