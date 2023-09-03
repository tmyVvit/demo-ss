package com.terry.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Order(2)
@Component
public class Aspect2 {

    @Around("execution(public * com.terry.demo.service..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("aspect2 in");
        Object obj = joinPoint.proceed();
        System.out.println("aspect2 out");
        return obj;
    }
}
