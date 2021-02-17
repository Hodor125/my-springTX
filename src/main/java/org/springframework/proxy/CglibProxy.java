package org.springframework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.tx.TransactionManager;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/17
 * @description ：
 * @version: 1.0
 */
public class CglibProxy {
    //要代理的类
    private Class<?> targetClass;

    //需要被代理的方法名
    List<String> declaredMethods = new ArrayList<>();

    public CglibProxy(Class<?> targetClass, List<String> declaredMethods) {
        this.targetClass = targetClass;
        this.declaredMethods = declaredMethods;
    }

    public Object getProxyInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                //调用目标方法
                Object result = null;
                if(declaredMethods.contains(method.getName())) {
                    System.out.println("开启事务，关闭自动提交......");
                    Connection connection = TransactionManager.getThreadLocalConnection();
                    connection.setAutoCommit(false);
                    try {
                        result = methodProxy.invokeSuper(o, args);
                        System.out.println("手动提交事务......");
                        connection.commit();
                    } catch (Throwable throwable) {
                        System.out.println("回滚事务......");
                        connection.rollback();
//                        throwable.printStackTrace();
                    } finally {
                        connection.close();
                    }
                } else {
                    result = methodProxy.invokeSuper(o, args);
                }
                return result;
            }
        });
        return enhancer.create();
    }
}
