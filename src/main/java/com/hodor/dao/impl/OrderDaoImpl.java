package com.hodor.dao.impl;

import com.hodor.bean.Order;
import com.hodor.dao.OrderDao;
import org.springframework.annotation.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/13
 * @description ：
 * @version: 1.0
 */
@Repository
public class OrderDaoImpl implements OrderDao {
    @Override
    public List<Order> findOrders() {
        List<Order> ordersList = new ArrayList<>();
        ordersList.add(new Order(111, "图书", "2020-11-20", 128));
        ordersList.add(new Order(112, "苹果", "2020-11-20", 12811));
        ordersList.add(new Order(113, "锤子", "2020-11-20", 12821));
        ordersList.add(new Order(114, "小米", "2020-11-20", 12800));
        System.out.println("查询订单......");
        return ordersList;
    }

    @Override
    public int addOrder(Order order) {
        System.out.println("新增订单......");
        return 1;
    }
}
