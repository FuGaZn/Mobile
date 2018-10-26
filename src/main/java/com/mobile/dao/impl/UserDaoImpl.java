package com.mobile.dao.impl;

import com.mobile.dao.OrderDao;
import com.mobile.dao.UserDao;
import com.mobile.domain.Order;
import com.mobile.domain.User;
import com.mobile.util.db.DBUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

import javax.jws.soap.SOAPBinding;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public User get(int uid) {
        return null;
    }

    @Override
    public void update(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update users set balance="+user.getBalance();
        try{
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasBalance(int uid) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = "select balance from users where uid=" + uid;
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble(1);
                if (balance > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void call(int uid, int timelen) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);

        for (Order order : orders) {
            if (order.getCall_nums() > 0 && timelen > 0) {
                int a = order.getCall_nums() > timelen ? timelen : order.getCall_nums();
                order.setCall_nums(order.getCall_nums() - a);
                timelen -= a;
                orderDao.update(order);
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (timelen > 0) {
            double price = 0.5;
            User user = userDao.get(uid);
            for (Order order:orders){
                if(order.getCall_over_price()>0){
                    price = order.getCall_over_price();
                }
            }
            user.setBalance(user.getBalance() - timelen * price);
            userDao.update(user);
            if (user.getBalance() < 0) {
                System.out.println("您已欠费，保留接听业务。请尽快充值。");
            }
        }
    }

    @Override
    public void sendMsg(int uid) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);

        boolean b = false;
        for (Order order : orders) {
            if (order.getMessage_nums() > 0) {
                order.setMessage_nums(order.getMessage_nums()-1);
                orderDao.update(order);
                b=true;
                break;
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (b==false) {
            double price = 0.1;
            User user = userDao.get(uid);
            for (Order order:orders){
                if (order.getMsg_over_price()>0)
                    price = order.getMsg_over_price();

            }
            user.setBalance(user.getBalance() - price);
            userDao.update(user);
            if (user.getBalance() < 0) {
                System.out.println("您已欠费，保留接听业务。请尽快充值。");
            }
        }
    }

    @Override
    public void useFlow(int uid, int nums,String location) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);

        for (Order order : orders) {
            if ((order.getLocation()==null||order.getLocation().equals(location)) && order.getFlow_nums() > 0 && nums > 0) {
                int a = order.getFlow_nums() > nums ? nums : order.getCall_nums();
                order.setCall_nums(order.getCall_nums() - a);
                nums -= a;
                orderDao.update(order);
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (nums > 0) {
            double price = 5;   //国内流量价格
            boolean b = false;
            User user = userDao.get(uid);
            for (Order order:orders){
                if(order.getLocation()==null||order.getLocation().equals(location)){
                    price = Math.min(order.getFlow_over_price(),price);
                    b = true;
                }
            }
            if(b == false){
                if(user.getLocation().equals(location)){
                    price = 3;  //本地流量价格
                }
            }
            user.setBalance(user.getBalance() - nums/1024 * price);
            userDao.update(user);
            if (user.getBalance() < 0) {
                System.out.println("您已欠费，保留接听业务。请尽快充值。");
            }
        }
    }
}
