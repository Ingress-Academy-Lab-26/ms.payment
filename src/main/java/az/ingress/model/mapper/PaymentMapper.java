package az.ingress.model.mapper;

import az.ingress.dao.entity.PaymentEntity;
import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;

import java.time.LocalDateTime;
import java.util.List;

public enum PaymentMapper {
    PAYMENT_MAPPER;
    public static PaymentEntity buildPaymentEntity(PaymentDto paymentDto, PaymentStatus status){
        return  PaymentEntity.builder()
                .amount(paymentDto.getAmount())
                .status(status)
                .build();
    }

    public static PaymentDto buildPaymentDto(PaymentEntity paymentEntity){
        return  PaymentDto.builder()
                .amount(paymentEntity.getAmount())
                .build();
    }

    public static List<PaymentDto> buildPaymentDtoList(List<PaymentEntity> paymentEntityList){
        return  paymentEntityList.stream()
                .map(PaymentMapper::buildPaymentDto)
                .toList();
    }
}
