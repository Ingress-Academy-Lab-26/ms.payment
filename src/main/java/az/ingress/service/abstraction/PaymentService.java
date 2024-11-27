package az.ingress.service.abstraction;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService {
    void refundPayment(Long id);

    void processPayment(Long userId, PaymentDto paymentDto);

    List<PaymentDto> getPayments();

    PaymentStatus getPaymentStatus(Long id);
}
