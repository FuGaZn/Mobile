package com.mobile.util;

import com.mobile.domain.Order;

import java.util.List;

/**
 * date: 2018/10/27
 * author: fu jia xing 161250025
 * <p>
 * 月账单
 */
public class Bill {
    int uid;
    String uname;
    List<Order> orders;
    double sum;
    double balance;

    public Bill(int uid, String uname,List<Order> orders, double sum,double balance) {
        this.uid = uid;
        this.uname = uname;
        this.orders = orders;
        this.sum = sum;
        this.balance=balance;
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
        builder.append("用户名："+uname+"\n");
        builder.append("本月总消费："+sum+"元\n");
        builder.append("账目余额："+balance+"元\n");
        builder.append("本月订购套餐：\n");
        for (Order order:orders){
            builder.append(order.toString()+"\n");
        }
        return builder.toString();
    }
}
