package az.ingress.exception;

import lombok.Getter;

@Getter
public class PaymentProcessingException extends RuntimeException{
    private final int httpStatusCode;

    public PaymentProcessingException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
