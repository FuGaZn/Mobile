package com.mobile.domain;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public class User {
    private int uid;
    private String uname;
    private double balance;
    private String location;
    private String phone;

    public User(int i, String n, double balance,String location,String phone) {
        this.uid = i;
        this.uname = n;
        this.balance=balance;
        this.location=location;
        this.phone=phone;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return uname;
    }

    public int getId() {
        return uid;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        String res ="编号："+uid+"  姓名："+uname+"  手机号码："+phone+"  归属地："+location+"  账户余额："+balance;
        return res;
    }
}
