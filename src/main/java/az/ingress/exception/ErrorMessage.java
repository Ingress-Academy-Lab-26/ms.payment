package az.ingress.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    UNEXPECTED_ERROR("Unexpected error occurred"),
    CLIENT_ERROR("Exception from client"),
    NOT_FOUND_EXCEPTION("Payment not found"),
    REFUND_EXCEPTION("Refund operation failed"),
    PAYMENT_PROCESSING_EXCEPTION("Payment processing failed");

    private final String message;
}
