package az.ingress.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BalanceUpdateDto {
    private BigDecimal newBalance;
}
