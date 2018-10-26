package com.mobile.dao.impl;

import com.mobile.dao.PackageDao;
import com.mobile.domain.Package;
import com.mobile.util.db.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageDaoImpl implements PackageDao {
    @Override
    public void add(Package p) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "insert into Packages(pid,pname,message_nums,call_nums,flow_nums,pay,startTime,endTime,valid,msg_over_price,call_over_price,flow_over_price,location) values(?,?,?,?,?,?,?,?,?,?,?,?,?);";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,p.getPid());
            ps.setString(2, p.getPname());
            ps.setInt(3, p.getMessage_nums());
            ps.setInt(4, p.getCall_nums());
            ps.setInt(5, p.getFlow_nums());
            ps.setDouble(6, p.getPay());
            ps.setString(7, p.getStart());
            ps.setString(8, p.getEnd());
            ps.setBoolean(9, p.isValid());
            ps.setDouble(10,p.getMsg_over_price());
            ps.setDouble(11,p.getCall_over_price());
            ps.setDouble(12,p.getFlow_over_price());
            ps.setString(13,p.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtils.close(null, ps, conn);
        }
    }

    @Override
    public List<Package> showAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Package p = null;
        List<Package> packages = new ArrayList<>();
        String sql = "select * from packages";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                packages.add(new Package(rs.getInt(1),rs.getString(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getDouble(6),rs.getString(7),rs.getString(8),rs.getBoolean(9),
                        rs.getDouble(10),rs.getDouble(11),rs.getDouble(12),rs.getString(13)));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.close(rs,ps,conn);
        }
        return packages;
    }
}