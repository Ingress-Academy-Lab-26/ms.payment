package az.ingress.service.concrete;

import az.ingress.client.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    AuthClient authClient;
    public void verifyToken(String accessToken) {
        authClient.verifyToken(accessToken);
    }
}
