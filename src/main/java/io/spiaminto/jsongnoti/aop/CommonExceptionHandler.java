package io.spiaminto.jsongnoti.aop;

import io.spiaminto.jsongnoti.mail.GmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

// service 패키지의 start 메서드 실행중 예외 발생시 에러 메일 발송
@RequiredArgsConstructor
@Slf4j
@Aspect @Component
public class CommonExceptionHandler {

    private final GmailSender gmailSender;

    @Pointcut("execution(* io.spiaminto.jsongnoti.service..start(..))")
    public void allServiceStart() {};

    @Around("allServiceStart()")
    public void execute(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            log.error("[CommonExceptionHandler] e = {}, message = {}", e.getClass().getName(), e.getMessage());
            gmailSender.sendError(e.getMessage());
        }
    }

}
