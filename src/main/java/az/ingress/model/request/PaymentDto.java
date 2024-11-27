package az.ingress.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentDto {
    private Long transactionId;
    private BigDecimal amount;
    private String pan;
    private String cvv;
    private Long cardId;
    private String cardHolderName;
    private String expirationDate;
    private String idempotencyKey;
}
