package az.ingress.service.abstraction;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.CacheData;

public interface PaymentCacheService {
    void save(long id, PaymentStatus status);
    CacheData get(String cacheKey);
}
