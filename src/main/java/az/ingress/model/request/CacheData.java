package az.ingress.model.request;

import az.ingress.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private PaymentStatus status;
}
