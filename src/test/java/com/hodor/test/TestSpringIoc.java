package com.hodor.test;

import com.hodor.bean.Order;
import com.hodor.controller.OrderController;
import com.hodor.service.OrderService;
import org.junit.Test;
import org.springframework.container.ClassPathXmlApplicationContext;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
public class TestSpringIoc {
    //测试获取包扫描路径
    @Test
    public void test1() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        OrderController orderController = (OrderController) context.getBean(OrderController.class);
        orderController.findOrders();
        orderController.addOrder();
//        OrderService myService = (OrderService) context.getBean("myService");
//        int i = myService.addOrder(new Order(113, "锤子", "2020-11-20", 12821));
//        System.out.println("结果：" + i);
    }
}
