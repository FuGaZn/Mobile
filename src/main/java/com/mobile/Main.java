package com.mobile;

import com.mobile.dao.PackageDao;
import com.mobile.dao.impl.PackageDaoImpl;
import com.mobile.domain.Package;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
      //  Package p = new Package(1,"短信套餐",200,0,0,10,"2018-11-01","2018-11-30",true);
        PackageDao packageDao = new PackageDaoImpl();
        try {
          //  packageDao.add(p);
            List<Package> list = new ArrayList<>();
            list = packageDao.showAll();
            for (Package a:list)
                System.out.println(a.toString());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}