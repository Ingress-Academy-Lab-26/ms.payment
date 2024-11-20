package az.ingress.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final int httpStatusCode;

    public AuthException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}