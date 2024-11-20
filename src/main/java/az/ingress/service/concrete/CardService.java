package az.ingress.service.concrete;

import az.ingress.client.CardClient;
import az.ingress.exception.RefundException;
import az.ingress.model.mapper.BalanceUpdateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static az.ingress.exception.ErrorMessage.REFUND_EXCEPTION;

@Service
@Slf4j
public class CardService {
    private CardClient cardClient;

    public boolean processCardPayment(String accessToken, BigDecimal productPrice) {
        BigDecimal currentBalance = getBalance(accessToken);

        if (currentBalance.compareTo(productPrice) >= 0) {
            updateBalance(accessToken, currentBalance.subtract(productPrice));
            return true;
        }

        return false;
    }

    private BigDecimal getBalance(String accessToken) {
        return cardClient.getBalance("Bearer " + accessToken);
    }

    private void updateBalance(String accessToken, BigDecimal newBalance) {
        cardClient.updateBalance("Bearer " + accessToken, BalanceUpdateMapper.buildBalanceUpdateDto(newBalance));
    }

    public void refundPayment(String accessToken, BigDecimal amount) {
        try {
            cardClient.refundPayment("Bearer " + accessToken, amount);
            log.info("ActionLog.info Refund successful for amount: {}", amount);
        } catch (Exception e) {
            log.error("ActionLog.error Refund failed for amount: {}. Error: {}", amount, e.getMessage());
            throw new RefundException(REFUND_EXCEPTION.getMessage(), 400);
        }
    }
}
