package com.mobile.dao;

import com.mobile.domain.Order;
import com.mobile.domain.User;
import com.mobile.util.Bill;

import java.util.List;

public interface UserDao {


    /**
     * 计算打电话的费用
     *
     * @param uid
     * @param timelen
     */
    public void call(int uid, int timelen);

    /**
     * 计算发短信的费用
     *
     * @param uid
     */
    public void sendMsg(int uid);

    /**
     * 计算使用流量的费用
     *
     * @param uid
     * @param nums
     * @param location
     */
    public void useFlow(int uid, int nums, String location);

    /**
     * 判断是否还有余额
     *
     * @param uid
     * @return
     */
    public boolean hasBalance(int uid);

    /**
     * 更新客户信息
     *
     * @param user
     */
    public void update(User user);

    /**
     * 返回客户信息
     *
     * @param uid
     * @return
     */
    public User get(int uid);

    /**
     * 新增客户
     * @param user
     * @return
     */
    public boolean add(User user);

    /**
     * 返回某客户某月的账单
     *
     * @param uid
     * @param month
     * @return
     */
    public Bill monthBill(int uid, int month);
}
