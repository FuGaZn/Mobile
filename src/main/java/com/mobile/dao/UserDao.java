package com.mobile.dao;

import com.mobile.domain.User;
import com.mobile.util.BillExtend;

import java.util.List;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public interface UserDao {

    /**
     * 展示所有用户信息
     *
     * @return
     */
    public List<User> showAll();

    /**
     * 通过姓名获取用户（可能会有重名）
     *
     * @param name
     * @return
     */
    public List<User> getUserByName(String name);

    /**
     * 计算打电话的费用
     *
     * @param uid
     * @param timelen
     */
    public void call(int uid, double timelen);

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
    public void useFlow(int uid, double nums, String location);

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
     *
     * @param user
     * @return
     */
    public boolean add(User user);

    /**
     * 返回某客户某月的账单
     *
     * @param uid
     * @param year
     * @param month
     * @return
     */
    public BillExtend monthBill(int uid, String year, int month);
}
