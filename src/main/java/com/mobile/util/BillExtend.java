package com.mobile.util;

import com.mobile.domain.Order;

import java.util.List;

/**
 * date: 2018/10/27
 * author: fu jia xing 161250025
 * <p>
 * 月账单
 */
public class BillExtend {
    int uid;
    String uname;
    List<Order> orders;
    int month;
    double sum;
    double balance;

    public BillExtend(int uid, String uname, List<Order> orders, double sum, double balance, int month) {
        this.uid = uid;
        this.uname = uname;
        this.orders = orders;
        this.sum = sum;
        this.balance = balance;
        this.month = month;
    }

    public String getUname() {
        return uname;
    }

    public double getBalance() {
        return balance;
    }

    public int getUid() {
        return uid;
    }

    public double getSum() {
        return sum;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        builder.append(month + "月账单：\n");
        builder.append("用户名：" + uname + "\n");
        builder.append("月消费：" + sum + "元\n");
        builder.append("账目余额：" + balance + "元\n");
        if (orders == null || orders.size() == 0)
            builder.append("尚未订购套餐。");
        else {
            builder.append("本月订购套餐：\n");
            for (Order order : orders) {
                builder.append(order.toString() + "\n");
            }
        }
        return builder.toString();
    }
}
