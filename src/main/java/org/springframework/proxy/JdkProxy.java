package org.springframework.proxy;

import org.springframework.aop.ProceedingJoinPoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/16
 * @description ：JdkProxy动态代理
 * @version: 1.0
 */
public class JdkProxy<T> {
    /**
     * 目标类class
     */
    private Class<?> targetClass;

    /**
     * aop切面类class
     */
    private Class<?> aopClass;

    /**
     * 目标对象
     */
    private Object targetObject;

    /**
     * 要被代理的方法的名字
     */
    String methodName;

    /**
     * AOP切面类的方法（带@Around注解的）
     */
    Method aopMethod;

    public JdkProxy(Class<?> targetClass, Class<?> aopClass, Object targetObject, String methodName, Method aopMethod) {
        this.targetClass = targetClass;
        this.aopClass = aopClass;
        this.targetObject = targetObject;
        this.methodName = methodName;
        this.aopMethod = aopMethod;
    }

    /**
     * 核心的动态代理类
     * 如果是目标的方法就封装jointpoint作为参数，反射aop切面类的class调用aop切面类中的增强方法
     * 同时传入的jointPoint参数的process方法反射了目标类的class，执行了目标类的目标方法（套娃），在这个动作的前后可以执行增强（查看LogAop切面类）
     * 获取jdk代理对象
     * @return
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), new InvocationHandler() {
            //
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //调用目标对象的目标方法
                if(methodName.equals(method.getName())) {
                    ProceedingJoinPoint joinPoint = new ProceedingJoinPoint(method, targetObject, args);
                    return aopMethod.invoke(aopClass.newInstance(), joinPoint);
                } else {
                    return method.invoke(targetObject, args);
                }
            }
        });
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<?> getAopClass() {
        return aopClass;
    }

    public void setAopClass(Class<?> aopClass) {
        this.aopClass = aopClass;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method getMethod() {
        return aopMethod;
    }

    public void setMethod(Method aopMethod) {
        this.aopMethod = aopMethod;
    }
}
