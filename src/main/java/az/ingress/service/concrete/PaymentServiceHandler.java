package az.ingress.service.concrete;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.dao.repository.PaymentRepository;
import az.ingress.exception.NotFoundException;
import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.CacheData;
import az.ingress.model.request.PaymentDto;
import az.ingress.service.abstraction.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static az.ingress.exception.ErrorMessage.NOT_FOUND_EXCEPTION;
import static az.ingress.model.enums.PaymentStatus.*;
import static az.ingress.model.mapper.ObjectMapperFactory.OBJECT_MAPPER;
import static az.ingress.model.mapper.PaymentMapper.PAYMENT_MAPPER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceHandler implements PaymentService {
    private final AmqpTemplate amqpTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentCacheServiceHandler paymentCacheService;
    private final RestTemplate restTemplate;

    @Value("${rabbitmq.payment.queue}")
    private String paymentQueue;

    @Value("${rabbitmq.refund.queue}")
    private String refundQueue;

    @Override
    public void refundPayment(Long id) {
        PaymentEntity paymentEntity = fetchPaymentIfExist(id);
        paymentEntity.setStatus(REFUNDED);
        paymentRepository.save(paymentEntity);
        paymentCacheService.save(id, REFUNDED);
        amqpTemplate.convertAndSend(refundQueue, paymentEntity);
        log.info("Refund operation queued for payment Id: {}", id);
    }

    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 1000,
                    maxDelay = 10000,
                    multiplier = 2.0
            )
    )
    @Override
    public void processPayment(Long userId, PaymentDto paymentDto) {
        PaymentEntity paymentEntity = PAYMENT_MAPPER.buildPaymentEntity(paymentDto, PENDING);
        paymentEntity.setUserId(userId);
        paymentEntity.setIdempotencyKey(UUID.randomUUID().toString());
        paymentRepository.save(paymentEntity);
        sendPaymentRequestToQueue(paymentDto);
    }

    @SneakyThrows
    private void sendPaymentRequestToQueue(PaymentDto paymentDto) {
        var paymentDtoJson = OBJECT_MAPPER.getInstance().writeValueAsString(paymentDto);
        amqpTemplate.convertAndSend(paymentQueue, paymentDtoJson);
    }

//    @Override
//    public void refundPayment(Long id) {
//        PaymentEntity paymentEntity = fetchPaymentIfExist(id);
//        paymentEntity.setStatus(REFUNDED);
//        paymentRepository.save(paymentEntity);
//        paymentCacheService.save(id, REFUNDED);
//        amqpTemplate.convertAndSend("refundQueue", paymentEntity);
//        log.info("ActionLog.info Message: Refund operation queued for payment Id: {}", id);
//    }

    private PaymentEntity fetchPaymentIfExist(Long id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_EXCEPTION.getMessage(), 404)
        );
    }

//    @Retryable(
//            maxAttempts = 5,
//            backoff = @Backoff(
//                    delay = 1000,
//                    maxDelay = 10000,
//                    multiplier = 2.0
//            )
//    )
//    @Override
//    public void processPayment(PaymentDto paymentDto) {
//        paymentRepository.save(PAYMENT_MAPPER.buildPaymentEntity(paymentDto, PENDING));
//        sendPaymentRequestToQueue(paymentDto);
//    }

//    @SneakyThrows
//    private void sendPaymentRequestToQueue(PaymentDto paymentDto) {
//        var paymentDtoJson = OBJECT_MAPPER.getInstance().writeValueAsString(paymentDto);
//        amqpTemplate.convertAndSend("paymentQueue", paymentDtoJson);
//    }

    @Override
    public List<PaymentDto> getPayments() {
        List<PaymentDto> paymentDtos = new ArrayList<>();

        paymentRepository.findAll().forEach(paymentEntity -> {
            paymentDtos.add(PAYMENT_MAPPER.buildPaymentDto(paymentEntity));
        });

        return paymentDtos;
    }

    @Override
    public PaymentStatus getPaymentStatus(Long id) {
        var cacheKey = "ms-payment:paymentId:" + id;
        PaymentStatus status = null;

        try {
            CacheData cacheData = paymentCacheService.get(cacheKey);

            if (cacheData != null) {
                status = cacheData.getStatus();
            }

            if (status == null) {
                PaymentEntity paymentEntity = fetchPaymentIfExist(id);
                status = paymentEntity.getStatus();

                paymentCacheService.save(id, status);
            }
        }
        catch (Exception e) {
            log.error("ActionLog.error Error retrieving payment status for paymentId: {} from cache, falling back to database. Error: {}", id, e.getMessage());

            PaymentEntity paymentEntity = fetchPaymentIfExist(id);
            status = paymentEntity.getStatus();
        }

        if (status == null) {
            throw new NotFoundException(NOT_FOUND_EXCEPTION.getMessage(), 404);
        }

        return status;
    }


}