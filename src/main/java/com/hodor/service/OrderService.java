package com.hodor.service;

import com.hodor.bean.Order;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
public interface OrderService {
    List<Order> findOrders();

    //给这个方法织入AOP
    int addOrder(Order order);
}
