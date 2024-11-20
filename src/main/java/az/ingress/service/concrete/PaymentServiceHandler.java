package az.ingress.service.concrete;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.dao.repository.PaymentRepository;
import az.ingress.exception.NotFoundException;
import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.mapper.PaymentMapper;
import az.ingress.model.request.CacheData;
import az.ingress.model.request.PaymentDto;
import az.ingress.service.abstraction.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static az.ingress.exception.ErrorMessage.NOT_FOUND_EXCEPTION;
import static az.ingress.model.enums.PaymentStatus.REFUNDED;
import static az.ingress.model.enums.PaymentStatus.PENDING;
import static az.ingress.model.mapper.PaymentMapper.buildPaymentEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceHandler implements PaymentService {
    private final AmqpTemplate amqpTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentCacheService paymentCacheService;

    @Override
    public void refundPayment(String accessToken, Long id) {
        PaymentEntity paymentEntity = fetchPaymentIfExist(id);
        paymentEntity.setStatus(REFUNDED);
        paymentRepository.save(paymentEntity);
        paymentCacheService.save(id, REFUNDED);
        amqpTemplate.convertAndSend("refundQueue", paymentEntity);
        log.info("ActionLog.info Message: Refund operation queued for payment Id: {}", id);
    }

    private PaymentEntity fetchPaymentIfExist(Long id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_EXCEPTION.getMessage(), 404)
        );
    }

    @Override
    public void processPayment(String accessToken, PaymentDto paymentDto) {
        PaymentEntity savedPayment = paymentRepository.save(buildPaymentEntity(paymentDto, PENDING));
        amqpTemplate.convertAndSend("paymentQueue", savedPayment);
    }

    @Override
    public List<PaymentDto> getPayments(String accessToken) {
        List<PaymentDto> paymentDtos = new ArrayList<>();

        paymentRepository.findAll().forEach(paymentEntity -> {
            paymentDtos.add(PaymentMapper.buildPaymentDto(paymentEntity));
        });

        return paymentDtos;
    }

    @Override
    public PaymentStatus getPaymentStatus(String accessToken, Long id) {
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