package com.hodor.dao;

import com.hodor.bean.Order;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/13
 * @description ：
 * @version: 1.0
 */
public interface OrderDao {
    List<Order> findOrders();

    int addOrder(Order order);
}
