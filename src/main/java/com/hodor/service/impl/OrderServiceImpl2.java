package com.hodor.service.impl;

import com.hodor.bean.Order;
import com.hodor.service.OrderService;
import org.springframework.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
@Service(value = "myService2")
public class OrderServiceImpl2 implements OrderService {
    @Override
    public List<Order> findOrders() {
        List<Order> ordersList = new ArrayList<>();
        ordersList.add(new Order(111, "图书", "2020-11-20", 128));
        ordersList.add(new Order(112, "苹果", "2020-11-20", 12811));
        ordersList.add(new Order(113, "锤子", "2020-11-20", 12821));
        ordersList.add(new Order(114, "小米", "2020-11-20", 12800));
        return ordersList;
    }

    @Override
    public int addOrder(Order order) {
        System.out.println("新增订单......");
        return 0;
    }
}
