package com.mobile.dao;

import com.mobile.domain.Package;

import java.sql.SQLException;
import java.util.List;

public interface PackageDao {
    public void add(Package p) throws SQLException;

    public List<Package> showAll() throws SQLException;
}
