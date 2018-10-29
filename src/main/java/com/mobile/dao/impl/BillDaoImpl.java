package com.mobile.dao.impl;

import com.mobile.dao.BillDao;
import com.mobile.domain.Bill;
import com.mobile.util.db.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * date: 2018/10/28
 * author: fu jia xing  161250025
 */
public class BillDaoImpl implements BillDao {
    @Override
    public Bill get(int uid, String year, int month) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from bills where uid=?";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, uid);
            rs = ps.executeQuery();
            if (rs.next()) {
                if(rs.getString(3).equals(year) && rs.getInt(4)==month) {
                    Bill bill = new Bill(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
                            rs.getDouble(5), rs.getDouble(6));
                    return bill;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(Bill bill) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "insert into bills(bid,uid,year,month,balance,expense) values(?,?,?,?,?,?);";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 0);
            ps.setInt(2, bill.getUid());
            ps.setString(3, bill.getYear());
            ps.setInt(4, bill.getMonth());
            ps.setDouble(5, bill.getBalance());
            ps.setDouble(6, bill.getExpense());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Bill bill) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE users set balance=?, expense=?;";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, bill.getBalance());
            ps.setDouble(2, bill.getBalance());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

