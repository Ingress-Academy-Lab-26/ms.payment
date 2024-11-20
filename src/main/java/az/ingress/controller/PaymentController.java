package az.ingress.controller;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;
import az.ingress.service.abstraction.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;



import java.util.List;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/v1/payments/")
@RequiredArgsConstructor
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    @PreAuthorize("@authServiceHandler.verifyToken(#accessToken)")
    @ResponseStatus(ACCEPTED)
    public void createPayment(@RequestHeader String accessToken, @RequestParam Long userId, @RequestBody PaymentDto paymentDto){
        paymentService.processPayment(userId, paymentDto);
    }

    @GetMapping()
    @PreAuthorize("@authServiceHandler.verifyToken(#accessToken)")
    public List<PaymentDto> getPayments(@RequestHeader String accessToken){
        return paymentService.getPayments();
    }

    @GetMapping("{id}")
    @PreAuthorize("@authServiceHandler.verifyToken(#accessToken)")
    public PaymentStatus getPaymentStatus(@RequestHeader String accessToken, @PathVariable Long id){
        return paymentService.getPaymentStatus(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("@authServiceHandler.verifyToken(#accessToken)")
    public void refundPayment(@RequestHeader String accessToken, @PathVariable Long id){
        paymentService.refundPayment(id);
    }
}
