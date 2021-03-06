package com.mobile.dao;

import com.mobile.domain.Order;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;

/**
 * date: 2018/10/26
 * author: fu jia xing
 */
public interface OrderDao {
    /**
     * 立刻订购套餐
     * @param uid
     * @param pid
     */
    public void subscribeNow(int uid,int pid);

    /**
     * 从下个月起订购套餐
     * @param uid
     * @param pid
     */
    public void subscribeNextMonth(int uid,int pid);

    /**
     * 查询一个客户的所有套餐（包括历史套餐）
     * @param uid
     * @return
     */
    public List<Order> myOrders(int uid);

    /**
     * 查询一个客户某一个月所有套餐
     * @param uid
     * @return
     */
    public List<Order> myMonthOrders(int uid,int month);

    /**
     * 立即退订套餐
     * @param pid
     * @param uid
     */
    public void unsubscribeNow(int uid, int pid);

    /**
     * 下月退订套餐
     * @param pid
     * @param uid
     */
    public void unsubscribeNextMonth(int uid, int pid);

    /**
     * 更新已订购的套餐的信息
     * @param order
     */
    public void update(Order order);

    /**
     * 添加个人订购的套餐
     * @param order
     */
    public void add(Order order);

    /**
     * 初始化
     */
    public void init();
}
