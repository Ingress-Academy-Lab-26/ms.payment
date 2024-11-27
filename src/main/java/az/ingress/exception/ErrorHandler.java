package az.ingress.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static az.ingress.exception.ErrorMessage.UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception ex) {
        log.error("Exception: ", ex);
        return new ErrorResponse(UNEXPECTED_ERROR.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(NotFoundException ex) {
        log.error("NotFoundException ", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    public ErrorResponse handle(PaymentProcessingException ex) {
        log.error("PaymentProcessingException ", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RefundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(RefundException ex) {
        log.error("RefundException ", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: ", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomFeignException.class)
    public ResponseEntity<ErrorResponse> handle(CustomFeignException ex) {
        log.error("CustomFeignException: ", ex);
        return ResponseEntity
                .status(ex.getHttpStatusCode())
                .body(new ErrorResponse(ex.getMessage()));
    }
}