package org.springframework.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/16
 * @description ：模仿ProceedingJoinPoint类
 * @version: 1.0
 */
public class ProceedingJoinPoint {
    //目标方法
    private Method method;

    //目标对象
    private Object target;

    //目标方法参数
    private Object[] args;

    public ProceedingJoinPoint(Method method, Object target, Object[] args) {
        this.method = method;
        this.target = target;
        this.args = args;
    }

    public Object proceed() {
        try {
            //调用目标方法
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
