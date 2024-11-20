package az.ingress.service.abstraction;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService {
    void refundPayment(String accessToken, Long id);

    void processPayment(String accessToken, PaymentDto paymentDto);

    List<PaymentDto> getPayments(String accessToken);

    PaymentStatus getPaymentStatus(String accessToken, Long id);
}
