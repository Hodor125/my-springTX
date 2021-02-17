package com.hodor.service.impl;

import com.hodor.bean.Order;
import com.hodor.dao.OrderDao;
import com.hodor.service.OrderService;
import org.springframework.annotation.Autowired;
import org.springframework.annotation.Service;
import org.springframework.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
@Service(value = "myService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Override
    public List<Order> findOrders() {
//        List<Order> ordersList = new ArrayList<>();
//        ordersList.add(new Order(111, "图书", "2020-11-20", 128));
//        ordersList.add(new Order(112, "苹果", "2020-11-20", 12811));
//        ordersList.add(new Order(113, "锤子", "2020-11-20", 12821));
//        ordersList.add(new Order(114, "小米", "2020-11-20", 12800));
        List<Order> ordersList = orderDao.findOrders();
        return ordersList;
    }

    @Override
    public int addOrder(Order order) {
        orderDao.addOrder(order);
        return 1;
    }

    @Transactional
    @Override
    public int transfer(String fromName, String toName, double Money) {
        int transfer1 = orderDao.transfer(fromName, -Money);
        System.out.println(transfer1 > 0 ? "转账成功" : "转账失败");
        System.out.println("=============================");
        int i = 1 / 0;
        int transfer2 = orderDao.transfer(toName, Money);
        System.out.println(transfer2 > 0 ? "收款成功" : "收款失败");
        return 1;
    }
}
