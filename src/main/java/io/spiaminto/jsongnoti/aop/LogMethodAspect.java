package io.spiaminto.jsongnoti.aop;

import io.spiaminto.jsongnoti.aop.trace.LogTrace;
import io.spiaminto.jsongnoti.aop.trace.TraceStatus;
import io.spiaminto.jsongnoti.mail.GmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@ConditionalOnExpression("${logging.trace.enabled:true}") // properties 값 읽어서 끔 (테스트에서 끄려고)
@RequiredArgsConstructor
public class LogMethodAspect {

    private final LogTrace logTrace;
    private final GmailSender gmailSender;

    // Pointcut 표현식 분리
    @Pointcut("execution(* io.spiaminto.jsongnoti.service..*(..))")
    public void allService() {}

    @Pointcut("execution(* io.spiaminto.jsongnoti.extractor..*(..))")
    public void allExtractor() {};

    @Pointcut("execution(* io.spiaminto.jsongnoti.filter..*(..))")
    public void allFilter() {};

    @Pointcut("execution(* io.spiaminto.jsongnoti.repository..*(..))")
    public void allRepository() {};


    @Around("allService() || allFilter() || allExtractor() || allRepository()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        Object[] params = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            params = joinPoint.getArgs();

            status = logTrace.begin(message);

            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            // Exception 발생시, end 가 아닌 exception 호출
            logTrace.exception(status, e, params);
            gmailSender.sendError(e.getMessage());
            throw e;
        }
    }
}
