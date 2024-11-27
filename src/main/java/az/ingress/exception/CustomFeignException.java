package az.ingress.exception;

import lombok.Getter;

@Getter
public class CustomFeignException extends RuntimeException {
    private final int httpStatusCode;

    public CustomFeignException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}