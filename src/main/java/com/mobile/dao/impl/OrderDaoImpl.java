package com.mobile.dao.impl;

import com.mobile.dao.OrderDao;
import com.mobile.domain.Order;
import com.mobile.util.db.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                        rs.getDouble(12),rs.getDouble(13),rs.getDouble(14),rs.getString(15)));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void subscribe(Order order) {

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
