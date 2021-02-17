package com.hodor.controller;

import com.hodor.bean.Order;
import com.hodor.service.OrderService;
import org.springframework.annotation.Autowired;
import org.springframework.annotation.Controller;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
@Controller
public class OrderController {
    @Autowired(value = "myService")
    OrderService orderService;

    public void findOrders() {
        List<Order> orders = orderService.findOrders();
        System.out.println(orders);
    }

    public void addOrder() {
        int i = orderService.addOrder(new Order(114, "小米", "2020-11-20", 12800));

    }
}
