package az.ingress.model.enums;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED;


    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
