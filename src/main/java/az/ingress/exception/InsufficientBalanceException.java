package az.ingress.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException{
    private final int httpStatusCode;

    public InsufficientBalanceException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
