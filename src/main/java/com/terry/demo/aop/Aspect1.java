package com.terry.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
public class Aspect1 {

    @Around("execution(public * com.terry.demo.service..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("aspect1 in");
        Object obj = joinPoint.proceed();
        System.out.println("aspect1 out");
        return obj;
    }
}
