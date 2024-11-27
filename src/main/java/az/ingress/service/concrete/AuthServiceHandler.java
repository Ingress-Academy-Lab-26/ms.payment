package az.ingress.service.concrete;

import az.ingress.client.AuthClient;
import az.ingress.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceHandler implements AuthService {
    AuthClient authClient;
    @Override
    public boolean verifyToken(String accessToken) {
        return authClient.verifyToken(accessToken);
    }
}
