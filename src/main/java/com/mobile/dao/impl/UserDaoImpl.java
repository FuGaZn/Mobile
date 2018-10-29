package com.mobile.dao.impl;

import com.mobile.dao.BillDao;
import com.mobile.dao.OrderDao;
import com.mobile.dao.UserDao;
import com.mobile.domain.Bill;
import com.mobile.domain.Order;
import com.mobile.domain.User;
import com.mobile.util.BillExtend;
import com.mobile.util.db.DBUtils;

import java.math.BigDecimal;
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
public class UserDaoImpl implements UserDao {

    @Override
    public List<User> getUserByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from users where uname=?;";
        List<User> users = new ArrayList<>();
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getDouble(6)));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> showAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from users;";
        List<User> users = new ArrayList<>();
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getDouble(6)));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BillExtend monthBill(int uid, String year, int month) {
        UserDao userDao = new UserDaoImpl();
        OrderDao orderDao = new OrderDaoImpl();
        BillDao billDao = new BillDaoImpl();
        Bill bill = billDao.get(uid,year,month);
        User user = userDao.get(uid);
        List<Order> orders = new ArrayList<>(orderDao.myMonthOrders(uid, month));
        if(bill == null)
            return null;
        BillExtend billExtend = new BillExtend(uid, user.getName(), orders, bill.getExpense(), bill.getBalance(), month);
        return billExtend;
    }

    @Override
    public User get(int uid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from users where uid=?;";
        try {
            con = DBUtils.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, uid);
            rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getDouble(6));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean add(User user) {
        UserDao userDao = new UserDaoImpl();
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "insert into users(uid,uname,balance,location,phone,expense) values(?,?,?,?,?,0)";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ps.setString(2, user.getName());
            ps.setDouble(3, user.getBalance());
            ps.setString(4, user.getLocation());
            ps.setString(5, user.getPhone());
            ps.executeUpdate();
            System.out.println("注册成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void update(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update users set balance=?, expense=? where uid=?;";
        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, user.getBalance());
            ps.setDouble(2, user.getExpense());
            ps.setInt(3,user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
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
    public void call(int uid, double timelen) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);
        if (orders != null) {
            for (Order order : orders) {
                if (order.isValid() && order.getCall_nums() > 0 && timelen > 0) {
                    double a = order.getCall_nums() > timelen ? timelen : order.getCall_nums();
                    order.setCall_nums(order.getCall_nums() - a);
                    timelen -= a;
                    System.out.println("使用套餐：" + order.getPname() + "  扣除时长：" + a + "分钟  剩余时长：" + order.getCall_nums() + "分钟");
                    orderDao.update(order);
                }
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (timelen > 0) {
            double price = 0.5; //基础资费
            User user = userDao.get(uid);
            Order o = null;
            if (orders != null) {
                for (Order order : orders) {
                    if (order.isValid() && order.getCall_over_price() > 0) {
                        double p = price;
                        price = Math.min(price, order.getCall_over_price());
                        if (p < price)
                            o = order;
                    }
                }
            }
            user.setBalance(user.getBalance() - timelen * price);
            user.setExpense(user.getExpense() + timelen * price);
            userDao.update(user);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
            BillDao billDao = new BillDaoImpl();
            Bill bill = billDao.get(uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1);
            if (bill == null) {
                bill = new Bill(0, uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1, user.getBalance(), timelen*price);
                billDao.add(bill);
            }else {
                bill.setBalance(user.getBalance());
                bill.setExpense(bill.getExpense() + timelen*price);
                billDao.update(bill);
            }

            if (o != null)
                System.out.println("超出套餐外时长：" + timelen + "分钟  根据 " + o.getPname() + " 套餐，超出时长按照" + price + "元/分钟计费，共" + price * timelen + "元。账户余额"+user.getBalance()+"元。");
            else
                System.out.println("超出套餐外时长：" + timelen + "分钟  无可用套餐，超出时长按照" + price + "元/分钟计费，共" + price * timelen  + "元。账户余额"+user.getBalance()+"元。");
            if (user.getBalance() < 0) {
                System.out.println("您已欠费" + user.getBalance() + "元，保留接听业务。请尽快充值。");
            }
        }
    }

    @Override
    public void sendMsg(int uid) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);

        boolean b = false;
        if (orders != null) {
            for (Order order : orders) {
                if (order.isValid() && order.getMessage_nums() > 0) {
                    order.setMessage_nums(order.getMessage_nums() - 1);
                    System.out.println("使用套餐：" + order.getPname() + "  剩余短信条数：" + order.getMessage_nums() + "条");
                    orderDao.update(order);
                    b = true;
                    break;
                }
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (b == false) {
            double price = 0.1;
            User user = userDao.get(uid);
            Order o = null;
            if (orders != null) {
                for (Order order : orders) {
                    if (order.isValid() && order.getMsg_over_price() > 0) {
                        double p = price;
                        price = Math.min(order.getMsg_over_price(), price);
                        if (p < price)
                            o = order;
                    }
                }
            }
            user.setBalance(user.getBalance() - price);
            user.setExpense(user.getExpense() + price);
            userDao.update(user);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
            BillDao billDao = new BillDaoImpl();
            Bill bill = billDao.get(uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1);
            if (bill == null) {
                bill = new Bill(0, uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1, user.getBalance(), price);
                billDao.add(bill);
            }else {
                bill.setBalance(user.getBalance());
                bill.setExpense(bill.getExpense() + price);
                billDao.update(bill);
            }

            if (o != null)
                System.out.println("超出套餐最多短信条数  根据 " + o.getPname() + " 套餐，超出短信按照" + price + "元/条计费，共" + price + "元");
            else
                System.out.println("超出套餐最多短信条数  无可用套餐，超出短信按照" + price + "元/条计费，共" + price + "元");
            if (user.getBalance() < 0) {
                System.out.println("您已欠费" + user.getBalance() + "元，保留接听业务。请尽快充值。");
            }
        }
    }

    public String flowToString(double a) {
        String res = "";
        double flow = a;
        if (flow < 1024) {
            res = flow + "K";
        } else if (flow < 1048576) {
            res = new BigDecimal(flow / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "M";
        } else
            res = new BigDecimal(flow / 1048576).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "G";
        return res;
    }


    @Override
    public void useFlow(int uid, double nums, String location) {
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);
        if (orders != null) {
            for (Order order : orders) {
                if (order.isValid() && (order.getLocation() == null || order.getLocation().equals(location)) && order.getFlow_nums() > 0 && nums > 0) {
                    double a = order.getFlow_nums() > nums ? nums : order.getCall_nums();
                    order.setFlow_nums(order.getFlow_nums() - a);
                    nums -= a;
                    orderDao.update(order);
                    System.out.println("使用套餐：" + order.getPname() + "  扣除流量：" + flowToString(a) + "  剩余流量：" + order.flowToString());
                }
            }
        }
        UserDao userDao = new UserDaoImpl();
        if (nums > 0) {
            double price = 0.5;   //国内流量价格
            User user = userDao.get(uid);
            Order o = null;
            if (orders != null) {
                for (Order order : orders) {
                    if (order.isValid() && (order.getLocation() == null || order.getLocation().equals(location))) {
                        double p = price;
                        price = Math.min(order.getFlow_over_price(), price);
                        if (p < price) {
                            o = order;
                        }
                    }
                }
            }
            if (o == null) {
                if (user.getLocation().equals(location)) {
                    price = 0.3;  //本地流量价格
                }
            }
            double out = new BigDecimal(nums / 1024 * price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            user.setBalance(user.getBalance()-out);
            user.setExpense(user.getExpense()+out);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
            BillDao billDao = new BillDaoImpl();
            Bill bill = billDao.get(uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1);
            if (bill == null) {
                bill = new Bill(0, uid, format1.format(Calendar.getInstance().getTime()), Calendar.getInstance().get(Calendar.MONTH) + 1, user.getBalance(), out);
                billDao.add(bill);
            }else {
                bill.setBalance(user.getBalance());
                bill.setExpense(bill.getExpense() + out);
                billDao.update(bill);
            }
            userDao.update(user);
            if (o != null)
                System.out.println("超出套餐可用流量  根据 " + o.getPname() + " 套餐，超出流量按照" + price + "元/M计费，共" +
                        out + "元");
            else {
                if (price == 0.3) {
                    System.out.println("超出套餐可用流量  无可用套餐，超出流量按照" + price + "元/M计费（本地流量），共" +
                            out + "元");
                } else
                    System.out.println("超出套餐可用流量  无可用套餐，超出流量按照" + price + "元/M计费（国内流量），共" +
                            out + "元");
            }

            if (user.getBalance() < 0) {
                System.out.println("您已欠费，保留接听业务。请尽快充值。");
            }
        }
    }
}
