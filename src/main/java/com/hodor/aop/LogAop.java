package com.hodor.aop;

import org.springframework.annotation.Around;
import org.springframework.annotation.Aspect;
import org.springframework.aop.ProceedingJoinPoint;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/16
 * @description ：切面类
 * @version: 1.0
 */
@Aspect
public class LogAop {
    @Around(execution = "com.hodor.service.impl.OrderServiceImpl.addOrder")
    public Object aroundAddOrder(ProceedingJoinPoint joinPoint) {
        Object result = null;
        //调用目标对象的目标方法
        try {
            System.out.println("==>前置通知......");
            result = joinPoint.proceed();
            System.out.println("==>返回通知......");
        } catch (Throwable throwable) {
            System.out.println("==>异常通知......" + throwable.getMessage());
        } finally {
            System.out.println("==>后置通知......");
        }
        return result;
    }

    @Around(execution = "com.hodor.service.impl.OrderServiceImpl.findOrders")
    public Object aroundfindOrders(ProceedingJoinPoint joinPoint) {
        Object result = null;
        //调用目标对象的目标方法
        try {
            System.out.println("==>前置通知......");
            result = joinPoint.proceed();
            System.out.println("==>返回通知......");
        } catch (Throwable throwable) {
            System.out.println("==>异常通知......" + throwable.getMessage());
        } finally {
            System.out.println("==>后置通知......");
        }
        return result;
    }
}
