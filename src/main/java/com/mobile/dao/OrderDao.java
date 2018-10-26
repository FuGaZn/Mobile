package com.mobile.dao;

import com.mobile.domain.Order;

import java.util.List;

public interface OrderDao {
    public void subscribe(Order order);

    public List<Order> myOrders(int uid);

    public void unsubscribeNow(Order order);

    public void unsubscribeNextMonth(Order order);

    public void update(Order order);
}
