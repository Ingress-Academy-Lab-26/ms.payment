package az.ingress.service.abstraction;

import az.ingress.client.dto.NotificationDto;
import az.ingress.model.request.PaymentDto;
import org.springframework.stereotype.Service;

@Service
public interface PaymentGateway {
    void processPayment(PaymentDto paymentDto);
    void processRefund(PaymentDto paymentDto);
}
