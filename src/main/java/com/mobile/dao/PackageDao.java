package com.mobile.dao;

import com.mobile.domain.Package;

import java.sql.SQLException;
import java.util.List;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public interface PackageDao {
    /**
     * 新增一种套餐
     *
     * @param p
     * @throws SQLException
     */
    public void add(Package p) throws SQLException;

    /**
     * 展示所有套餐
     *
     * @return
     * @throws SQLException
     */
    public List<Package> showAll() throws SQLException;
}
