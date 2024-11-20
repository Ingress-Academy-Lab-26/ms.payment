package az.ingress.service.concrete;

import az.ingress.client.dto.NotificationDto;
import az.ingress.model.request.PaymentDto;
import az.ingress.service.abstraction.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static az.ingress.client.enums.ChannelType.*;
import static az.ingress.model.mapper.ObjectMapperFactory.OBJECT_MAPPER;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentGatewayHandler implements PaymentGateway {
    private final RestTemplate restTemplate;
    private final AmqpTemplate amqpTemplate;

    @Override
    public void processPayment(PaymentDto paymentDto) {
        String paymentUrl = "https://payment.free.beeceptor.com";
        processBeeceptorRequest(paymentDto, paymentUrl, "Payment");
    }

    @Override
    public void processRefund(PaymentDto paymentDto) {
        String refundUrl = "https://refund.free.beeceptor.com";
        processBeeceptorRequest(paymentDto, refundUrl, "Refund");
    }

    private void processBeeceptorRequest(PaymentDto paymentDto, String url, String operationType) {
        String jsonRequestBody = convertPaymentDtoToJson(paymentDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        NotificationDto notificationDto = new NotificationDto();
        if (response.getStatusCode() == HttpStatus.OK) {
            notificationDto.setChannelType(MAIL);
            notificationDto.setPayload(operationType + " was successful. Transaction ID: " + paymentDto.getTransactionId());
        } else {
            notificationDto.setChannelType(TELEGRAM);
            notificationDto.setPayload(operationType + " failed. Transaction ID: " + paymentDto.getTransactionId());
        }

        sendNotificationToQueue(notificationDto);
    }


    @SneakyThrows
    private void sendNotificationToQueue(NotificationDto notificationDto) {
        String notificationJson = OBJECT_MAPPER.getInstance().writeValueAsString(notificationDto);
        amqpTemplate.convertAndSend("NOTIFICATION_Q", notificationJson);
    }

    @SneakyThrows
    private String convertPaymentDtoToJson(PaymentDto paymentDto) {
        return OBJECT_MAPPER.getInstance().writeValueAsString(paymentDto);
    }
}
