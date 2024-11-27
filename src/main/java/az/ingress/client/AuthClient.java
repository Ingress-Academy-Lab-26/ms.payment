package az.ingress.client;

import az.ingress.client.decoder.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(
        name = "ms-auth",
        path = "/internal",
        url = "${client.urls.ms-auth}",
        configuration = CustomErrorDecoder.class
)
public interface AuthClient {
    @PostMapping("/v1/token/verify")
    boolean verifyToken(@RequestHeader(AUTHORIZATION) String accessToken);

    /*
    @PostMapping("/validate-token")
    AuthResponseDto validateToken(@RequestHeader("Authorization") String accessToken);
    */
}
