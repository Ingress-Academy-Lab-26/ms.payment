package az.ingress.client;

import az.ingress.client.decoder.CustomErrorDecoder;
import az.ingress.model.request.BalanceUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(
        name = "ms.card",
        path = "/internal",
        url = "${client.urls.ms-card}",
        configuration = CustomErrorDecoder.class
)
public interface CardClient {
    @GetMapping("/balance/check")
    BigDecimal getBalance(@RequestHeader("Authorization") String authorization);

    @PostMapping("/balance/update")
    void updateBalance(@RequestHeader("Authorization") String authorization, @RequestBody BalanceUpdateDto balanceUpdateDto);


    @DeleteMapping("/balance/refund")
    void refundPayment(String s, BigDecimal amount);
}
