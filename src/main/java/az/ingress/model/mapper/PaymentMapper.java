package az.ingress.model.mapper;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;

import java.util.UUID;

public enum PaymentMapper {
    PAYMENT_MAPPER;
    public PaymentEntity buildPaymentEntity(PaymentDto paymentDto, PaymentStatus status){
        return  PaymentEntity.builder()
                .cardId(paymentDto.getCardId())
                .amount(paymentDto.getAmount())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }

    public PaymentDto buildPaymentDto(PaymentEntity paymentEntity){
        return  PaymentDto.builder()
                .amount(paymentEntity.getAmount())
                .build();
    }
}
