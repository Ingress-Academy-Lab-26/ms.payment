package az.ingress.exception;

import lombok.Getter;

@Getter
public class RefundException extends RuntimeException{
    private final int httpStatusCode;

    public RefundException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
