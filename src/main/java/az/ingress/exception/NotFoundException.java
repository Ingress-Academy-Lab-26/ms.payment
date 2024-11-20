package az.ingress.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final int httpStatusCode;

    public NotFoundException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
}
