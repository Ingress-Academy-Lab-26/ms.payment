package az.ingress.service.concrete;

import az.ingress.model.enums.PaymentStatus;
import az.ingress.model.request.CacheData;
import az.ingress.service.abstraction.PaymentCacheService;
import az.ingress.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCacheServiceHandler implements PaymentCacheService {
    private final CacheUtil cacheUtil;

    @Override
    public void save(long id, PaymentStatus status){
        var cacheKey = "ms-payment:paymentId:"+id;
        var data = new CacheData(status);
        cacheUtil.saveToCache(cacheKey, data, 1L, ChronoUnit.MINUTES);
    }

    @Override
    public CacheData get(String cacheKey){
        CacheData data = cacheUtil.getBucket(cacheKey);
        log.info("ActionLog.cacheData:{}",  data);
        return data;
    }
}
