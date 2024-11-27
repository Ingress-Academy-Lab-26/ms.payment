package az.ingress.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static az.ingress.model.mapper.ObjectMapperFactory.OBJECT_MAPPER;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("within(@az.ingress.aop.annotation.Log *)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = ((MethodSignature) joinPoint.getSignature());
        var parameters = OBJECT_MAPPER.getInstance().writeValueAsString(joinPoint.getArgs());
        logEvent("start", signature, parameters);
        Object response;
        try {
            response = joinPoint.proceed();
        } catch (Throwable throwable) {
            logEvent("error", signature, parameters);
            throw throwable;
        }
        logEndAction(signature, response);
        return response;
    }

    private void logEvent(String eventName, MethodSignature signature, String parameters) {
        log.info("ActionLog.{}.{} {}", signature.getName(), eventName, parameters);
    }

    private void logEndAction(MethodSignature signature, Object response) {
        if (void.class.equals(signature.getReturnType())) log.info("ActionLog.{}.end", signature.getName());
        else log.info("ActionLog.{}.end {}", signature.getName(), response);
    }
}