package az.ingress.service.abstraction;

public interface AuthService {
    boolean verifyToken(String accessToken);
}
