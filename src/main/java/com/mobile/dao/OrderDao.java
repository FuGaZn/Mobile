package com.mobile.dao;

import com.mobile.domain.Order;

import java.util.List;

public interface OrderDao {
    /**
     * 立刻订购套餐
     * @param pid
     */
    public void subscribeNow(int pid,int uid);

    /**
     * 从下个月起订购套餐
     * @param pid
     */
    public void subscribeNextMonth(int pid,int uid);

    /**
     * 查询一个客户的所有套餐（包括历史套餐）
     * @param uid
     * @return
     */
    public List<Order> myOrders(int uid);

    /**
     * 立即退订套餐
     * @param order
     */
    public void unsubscribeNow(Order order);

    /**
     * 下月退订套餐
     * @param order
     */
    public void unsubscribeNextMonth(Order order);

    /**
     * 更新已订购的套餐的信息
     * @param order
     */
    public void update(Order order);
}
