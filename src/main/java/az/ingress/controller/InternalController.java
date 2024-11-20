package az.ingress.controller;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.service.abstraction.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("internal/v1")
@RequiredArgsConstructor
public class InternalController {
    private final PaymentService paymentService;

    @GetMapping("/payments/{id}")
    public PaymentStatus getPaymentStatus(@RequestHeader String accessToken, @PathVariable Long id){
        return paymentService.getPaymentStatus(accessToken, id);
    }
}
