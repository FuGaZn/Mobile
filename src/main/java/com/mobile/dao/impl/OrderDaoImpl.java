package com.mobile.dao.impl;

import com.mobile.dao.OrderDao;
import com.mobile.domain.Order;
import com.mobile.domain.Package;
import com.mobile.util.TimeLen;
import com.mobile.util.db.DBUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

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
    public List<Order> myOrders(int uid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> list = new ArrayList<>();
        String sql = "select * from orders where uid=" + uid;
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Order(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getDouble(10), rs.getBoolean(11),
                        rs.getDouble(12), rs.getDouble(13), rs.getDouble(14), rs.getString(15)));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void subscribeNow(int pid, int uid) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String start = format.format(calendar.getTime());
        //  calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        //  String end = format.format(calendar.getTime());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from packages where pid=" + pid;
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String end = "";
                Package p = new Package(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getDouble(6), rs.getBoolean(7),
                        rs.getDouble(8), rs.getDouble(9), rs.getDouble(10), rs.getString(11), rs.getString(12));
                TimeLen timelen = p.getTimeLen();


                Order order = new Order(1, p.getPid(), uid, p.getPname(), p.getMessage_nums(), p.getCall_nums(), p.getFlow_nums(), start, end, p.getPay(), true, p.getMsg_over_price(), p.getCall_over_price(), p.getFlow_over_price(), p.getLocation());
            } else {
                System.out.println("套餐不存在。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribeNextMonth(int pid, int uid) {

    }

    @Override
    public void unsubscribeNextMonth(Order order) {

    }

    @Override
    public void unsubscribeNow(Order order) {

    }

    @Override
    public void update(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update order set endTime=?,valid=? where oid=" + order.getOid();
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
