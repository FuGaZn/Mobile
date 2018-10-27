package com.mobile.dao.impl;

import com.mobile.dao.OrderDao;
import com.mobile.dao.UserDao;
import com.mobile.domain.Order;
import com.mobile.domain.Package;
import com.mobile.domain.User;
import com.mobile.util.TimeLen;
import com.mobile.util.db.DBUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

import javax.jws.soap.SOAPBinding;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public class OrderDaoImpl implements OrderDao {

    @Override
    public List<Order> myMonthOrders(int uid, int month) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String start = format.format(calendar.getTime());
        calendar.set(Calendar.MONTH, month + 1);
        String end = format.format(calendar.getTime());

        String sql = "select * from orders where uid=? and ((startTime<=? and endTime>=?) or (startTime>=? and endTime<=?));";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            ps.setString(2, start);
            ps.setString(3, end);
            ps.setString(4, start);
            ps.setString(5, end);
            rs = ps.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                orders.add(new Order(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getDouble(10), rs.getBoolean(11),
                        rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getString(15), rs.getBoolean(16)));
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> myOrders(int uid) {
        UserDao userDao = new UserDaoImpl();
        User user = userDao.get(uid);
        if (user == null) {
            return null;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> list = new ArrayList<>();
        String sql = "select * from orders where uid=" + uid + ";";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Order(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getDouble(10), rs.getBoolean(11),
                        rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getString(15), rs.getBoolean(16)));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void subscribeNow(int uid, int pid) {
        UserDao userDao = new UserDaoImpl();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String start = format.format(calendar.getTime());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from packages where pid=" + pid + ";";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String end = "";
                Package p = new Package(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getDouble(6), rs.getBoolean(7),
                        rs.getDouble(8), rs.getDouble(9), rs.getDouble(10), rs.getString(11), rs.getString(12), rs.getBoolean(13));
                TimeLen timelen = p.getTimeLen();
                double pay = p.getPay();
                User user = userDao.get(uid);
                if (user == null) {
                    System.out.println("用户不存在。");
                    return;
                }
                if (user.getBalance() < pay) {
                    System.out.println("余额不足，暂时不能订购本套餐。请尽快充值");
                    return;
                } else {
                    user.setBalance(user.getBalance() - pay);
                    userDao.update(user);
                }

                //如果套餐时长不足一月，那么就从今天开始计算，终止时间为下个月第一天或者今天加上时长的后一天（看那个更小）
                if (timelen.getDay() > 0 && timelen.getMonth() == 0 && timelen.getYear() == 0) {
                    int len = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1;
                    if (len > timelen.getDay()) {
                        calendar.add(Calendar.DATE, timelen.getDay());
                    } else {
                        calendar.add(Calendar.DATE, len);
                    }
                    end = format.format(calendar.getTime());
                } else if (timelen.getDay() == 0 && (timelen.getMonth() > 0 || timelen.getYear() > 0)) { //如果套餐时长是n个月，就从今天开始到以后的第n个月第一天之前。
                    calendar.add(Calendar.MONTH, timelen.getMonth());
                    calendar.add(Calendar.YEAR, timelen.getYear());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    end = format.format(calendar.getTime());
                }

                Order order = new Order(1, p.getPid(), uid, p.getPname(), p.getMessage_nums(), p.getCall_nums(), p.getFlow_nums(), start, end, p.getPay(), true, p.getMsg_over_price(), p.getCall_over_price(), p.getFlow_over_price(), p.getLocation(), p.isNextMonthValid());

                String sql3 = "select startTime, endTime, valid from orders where pid=? and uid=?;";
                ps = conn.prepareStatement(sql3);
                ps.setInt(1, order.getPid());
                ps.setInt(2, order.getUid());
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getBoolean(3) == true && rs.getString(1).compareTo(order.getStart()) <= 0 && rs.getString(2).compareTo(order.getEnd()) >= 0) {
                        System.out.println("您已订购了相同时限内的本套餐。");
                        return;
                    }
                }

                String sql2 = "insert into orders(oid,pid,uid,pname,message_nums,call_nums,flow_nums,startTime,endTime,pay,valid,msg_over_price,call_over_price,flow_over_price,location,next_month) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, order.getOid());
                ps.setInt(2, order.getPid());
                ps.setInt(3, order.getOid());
                ps.setString(4, order.getPname());
                ps.setInt(5, order.getMessage_nums());
                ps.setDouble(6, order.getCall_nums());
                ps.setDouble(7, order.getFlow_nums());
                ps.setString(8, order.getStart());
                ps.setString(9, order.getEnd());
                ps.setDouble(10, order.getPay());
                ps.setBoolean(11, order.isValid());
                ps.setDouble(12, order.getMsg_over_price());
                ps.setDouble(13, order.getCall_over_price());
                ps.setDouble(14, order.getFlow_over_price());
                ps.setString(15, order.getLocation());
                ps.setBoolean(16, order.isNextMonthValid());
                ps.executeUpdate();
                System.out.println("您已成功订购本套餐");
            } else {
                System.out.println("套餐不存在。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribeNextMonth(int uid, int pid) {
        UserDao userDao = new UserDaoImpl();
        User user = userDao.get(uid);
        if (user == null) {
            System.out.println("用户不存在。");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String start = format.format(calendar.getTime());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from packages where pid=" + pid + ";";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String end = "";
                Package p = new Package(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getDouble(6), rs.getBoolean(7),
                        rs.getDouble(8), rs.getDouble(9), rs.getDouble(10), rs.getString(11), rs.getString(12), rs.getBoolean(13));
                TimeLen timelen = p.getTimeLen();
                if (timelen.getDay() > 0 && timelen.getMonth() == 0 && timelen.getYear() == 0) {
                    calendar.add(Calendar.DATE, timelen.getDay());
                    end = format.format(calendar.getTime());
                } else if (timelen.getDay() == 0 && (timelen.getMonth() > 0 || timelen.getYear() > 0)) {
                    calendar.add(Calendar.YEAR, timelen.getYear());
                    calendar.add(Calendar.MONTH, timelen.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    end = format.format(calendar.getTime());
                }

                Order order = new Order(1, p.getPid(), uid, p.getPname(), p.getMessage_nums(), p.getCall_nums(), p.getFlow_nums(), start, end, p.getPay(), true, p.getMsg_over_price(), p.getCall_over_price(), p.getFlow_over_price(), p.getLocation(), p.isNextMonthValid());

                String sql3 = "select startTime, endTime, valid from orders where pid=? and uid=?;";
                ps = conn.prepareStatement(sql3);
                ps.setInt(1, order.getPid());
                ps.setInt(2, order.getUid());
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getBoolean(3) == true && rs.getString(1).compareTo(order.getStart()) <= 0 && rs.getString(2).compareTo(order.getEnd()) >= 0) {
                        System.out.println("您已订购了相同时限内的本套餐。");
                        return;
                    }
                }

                String sql2 = "insert into orders(oid,pid,uid,pname,message_nums,call_nums,flow_nums,startTime,endTime,pay,valid,msg_over_price,call_over_price,flow_over_price,location,next_month) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
                ps = conn.prepareStatement(sql2);
                ps.setInt(1, order.getOid());
                ps.setInt(2, order.getPid());
                ps.setInt(3, order.getOid());
                ps.setString(4, order.getPname());
                ps.setInt(5, order.getMessage_nums());
                ps.setDouble(6, order.getCall_nums());
                ps.setDouble(7, order.getFlow_nums());
                ps.setString(8, order.getStart());
                ps.setString(9, order.getEnd());
                ps.setDouble(10, order.getPay());
                ps.setBoolean(11, order.isValid());
                ps.setDouble(12, order.getMsg_over_price());
                ps.setDouble(13, order.getCall_over_price());
                ps.setDouble(14, order.getFlow_over_price());
                ps.setString(15, order.getLocation());
                ps.setBoolean(16, order.isNextMonthValid());
                ps.executeUpdate();
                System.out.println("您已成功订购本套餐");
            } else {
                System.out.println("套餐不存在。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsubscribeNextMonth(int uid, int pid) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> list = new ArrayList<>(orderDao.myOrders(uid));
        for (Order order: list){
            if(order.getPid() == pid && order.isValid()){
                order.setNext_month(false);
                orderDao.update(order);
            }
        }
    }



    @Override
    public void unsubscribeNow(int uid, int pid) {

    }

    @Override
    public void update(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update order set endTime=?,valid=? where oid=" + order.getOid() + ";";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, order.getEnd());
            ps.setBoolean(2, order.isValid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
