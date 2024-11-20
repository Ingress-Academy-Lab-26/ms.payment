package az.ingress.service.abstraction;

import az.ingress.dao.entity.PaymentEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentProcessor {
    void processPayment(String accessToken, PaymentEntity paymentEntity);
    void processRefund(String accessToken, PaymentEntity paymentEntity);
}
