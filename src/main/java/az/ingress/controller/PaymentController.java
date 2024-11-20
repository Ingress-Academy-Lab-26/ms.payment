package az.ingress.controller;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.PaymentDto;
import az.ingress.service.abstraction.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/v1/payments/")
@RequiredArgsConstructor
public class PaymentController {
    PaymentService paymentService;

    @PostMapping
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public ResponseEntity<String> createPayment(@RequestHeader String accessToken, @RequestBody PaymentDto paymentDto){
        if(paymentDto.getAmount()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment details");
        }
        
        paymentService.processPayment(accessToken, paymentDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment accepted");
    }

    @GetMapping()
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public List<PaymentDto> getPayments(@RequestHeader String accessToken){
        return paymentService.getPayments(accessToken);
    }

    @GetMapping("{id}")
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public PaymentStatus getPaymentStatus(@RequestHeader String accessToken, @PathVariable Long id){

        return paymentService.getPaymentStatus(accessToken, id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public void refundPayment(@RequestHeader String accessToken, @PathVariable Long id){
        paymentService.refundPayment(accessToken, id);
    }
}
