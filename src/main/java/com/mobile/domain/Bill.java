package com.mobile.domain;

/**
 * date: 2018/10/26
 * author: fu jia xing 161250025
 */
public class Bill {
    int bid;
    int uid;
    String year;
    int month;
    double balance;
    double expense;

    public Bill(int bid, int uid, String year, int month, double balance, double expense) {
        this.bid = bid;
        this.uid = uid;
        this.year = year;
        this.month = month;
        this.balance = balance;
        this.expense = expense;
    }

    public double getBalance() {
        return balance;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getBid() {
        return bid;
    }

    public int getUid() {
        return uid;
    }

    public int getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public double getExpense() {
        return expense;
    }
}
