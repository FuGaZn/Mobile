package com.mobile;

import com.mobile.dao.OrderDao;
import com.mobile.dao.PackageDao;
import com.mobile.dao.UserDao;
import com.mobile.dao.impl.OrderDaoImpl;
import com.mobile.dao.impl.PackageDaoImpl;
import com.mobile.dao.impl.UserDaoImpl;
import com.mobile.domain.Order;
import com.mobile.domain.Package;
import com.mobile.domain.User;
import com.mobile.util.Bill;
import com.mobile.util.TimeLen;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        Scanner sc = new Scanner(System.in);
        System.out.println("------------------------------------------");
        System.out.println("Input order:");
        while (sc.hasNext()) {
            String orderline = sc.nextLine();
            String order1 = orderline.split("\\s+")[0];
            switch (order1) {
                /**-----------------------------------------------
                 * 用户注册操作：
                 * 命令：register [user](name, balance, location, phone)
                 *
                 * 实例：register user(小明, 100, 江苏省, 19384385353)
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "register":
                    main.register(orderline);
                    break;

                /**-----------------------------------------------
                 * 套餐创建操作：
                 * 命令：
                 * create [package](name, msg_num,call_num, flow_num, pay, msg_over_price, call_over_price, flow_over_price, location, timeLen)
                 * 其中，必填选项为name, pay, location, timeLen。其余选项可视情况添加。
                 * 但是，如果某一项不填，其前后的区分符逗号不能不填。
                 *
                 * 实例： create package(短信套餐, 200, , , 10, 0.1, , ,江苏省, 0.1.0)
                 * 详情见说明文档
                 * -----------------------------------------------
                 */
                case "create":
                    main.createPackage(orderline);
                    break;

                /**-----------------------------------------------
                 * show操作：用于展示套餐信息或者客户信息
                 * 命令:
                 * show -u                      展示所有的客户
                 * show -p                      展示已经创建的所有套餐
                 * show -p uid                  展示某客户的所有套餐（包括历史记录）
                 * show -p -n username          输入客户姓名，展示某客户的所有套餐
                 * show -b uid [month]          展示某客户的月账单（默认为本月账单，后面加上n后，返回前面的第n月的账单）
                 *
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "show":
                    main.show(orderline);
                    break;

                /**-----------------------------------------------
                 * 用户订购套餐操作：
                 * 命令：order uid pid [next]
                 * uid为客户编号
                 * pid为套餐编号
                 * 默认从本月开始订购，加上next表示从下月开始订购
                 *
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "order":
                    main.order(orderline);
                    break;

                /**-----------------------------------------------
                 * 退订套餐操作：
                 * 命令：unsub uid pid [next]
                 * uid  客户编号
                 * pid  套餐编号
                 * next  可选参数，默认是从本月开始退订，加上next表示从下月开始退订
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "unsub":
                    break;

                /**-----------------------------------------------
                 * 用户拨打电话时的资费生成：
                 * 命令：call uid minutes
                 * uid      客户编号
                 * minutes  拨打电话的时长
                 * 返回信息：使用的套餐、扣除的费用、余额
                 *
                 * 实例：call 1 1.5
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "call":
                    main.call(orderline);
                    break;
                /**-----------------------------------------------
                 * 用户发送短信时的资费生成：
                 * 命令：send uid
                 * uid  客户编号
                 * 返回信息：使用的套餐、扣除的费用、余额
                 *
                 * 实例：send 1
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "send":
                    main.sendMsg(orderline);
                    break;
                /**-----------------------------------------------
                 * 用户使用流量时的资费生成：
                 * 命令：surf uid num [location]
                 * uid      客户编号
                 * num      使用的流量数量（单位可以是G/M/K)
                 * location 当前使用流量的地区。（默认为本地，如果是在外省，将会使用国内流量）
                 * 返回信息：使用的套餐、扣除的费用、余额
                 *
                 * 实例：surf 1 3M 上海市
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "surf":
                    main.surf(orderline);
                    break;

                /**-----------------------------------------------
                 * 期初建账：每个月1号的时候对所有数据进行初始化，将会为客户生成新的套餐和取消旧的套餐
                 * 命令：init
                 *
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "init":
                    break;

            }
            System.out.println("------------------------------------------");
            System.out.println("Input order:");

        }
    }

    /**
     * @param name     客户姓名
     * @param balance  账户余额
     * @param location 手机号归属地
     * @param phone    手机号
     * @return
     */
    private void register(String name, double balance, String location, String phone) {
        long time1 = System.currentTimeMillis();

        User user = new User(0, name, balance, location, phone, 0);
        UserDao userDao = new UserDaoImpl();
        userDao.add(user);

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 创建一个新的套餐
     *
     * @param pname           套餐名称
     * @param message_num     套餐内所含的最多可以发送的短信条数，单位：条
     * @param call_num        最多可以拨打的电话时长，单位：分钟
     * @param flow_num        最多可以使用的流量数，单位：K
     * @param pay             月功能费
     * @param msg_over_price  发送短信超出条数的价格，单位：元/条
     * @param call_over_price 拨打电话超出时间的价格，单位：元/分钟
     * @param flow_over_price 使用流量超出数量的价格，单位：元/M
     * @param location        套餐归属地（国内套餐默认为null)
     * @param timeLen         套餐期限（可以是月套餐，也可以是一日无限包、十日流量包等短期套餐包）
     *                        timelen的格式："0.0.0"    从左到右分别表示年、月、日数
     */
    private void createPackage(String pname, int message_num, int call_num, int flow_num, double pay, double msg_over_price,
                               double call_over_price, double flow_over_price, String location, String timeLen) {
        double time1 = System.currentTimeMillis();

        Package p = new Package(0, pname, message_num, call_num, flow_num, pay, true, msg_over_price, call_over_price, flow_over_price, location, timeLen, true);
        PackageDao packageDao = new PackageDaoImpl();
        try {
            packageDao.add(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 生成月账单
     *
     * @param uid
     * @param month
     */
    private void showMonthBill(int uid, int month) {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        Bill bill = userDao.monthBill(uid, month);
        if (bill == null) {
            System.out.println("该用户尚未订购任何套餐。");
        }
        System.out.println(bill.toString());

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 展示所有用户的信息
     */
    private void showAllUsers() {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        List<User> users = userDao.showAll();
        for (User user : users) {
            System.out.println(user.toString());
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 展示所有套餐的信息
     */
    private void showAllPackages() {
        double time1 = System.currentTimeMillis();

        PackageDao packageDao = new PackageDaoImpl();
        try {
            List<Package> packages = packageDao.showAll();
            for (Package p : packages) {
                System.out.println(p.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 通过客户编号查询其所有已订购套餐
     *
     * @param uid
     */
    private void showMyPackages(int uid) {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        User user = userDao.get(uid);
        if (user == null) {
            System.out.println("用户不存在。");
            double timelen = (System.currentTimeMillis() - time1) / 1000;
            DecimalFormat df = new DecimalFormat("0.000");
            System.out.println("Time: " + df.format(timelen) + "s");
            return;
        }
        OrderDao orderDao = new OrderDaoImpl();
        List<Order> orders = orderDao.myOrders(uid);
        if (orders == null || orders.size() == 0) {
            System.out.println("此用户尚未订购任何套餐。");
            double timelen = (System.currentTimeMillis() - time1) / 1000;
            DecimalFormat df = new DecimalFormat("0.000");
            System.out.println("Time: " + df.format(timelen) + "s");
            return;
        }
        for (Order order : orders)
            System.out.println(order.toString());

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 通过用户姓名查询其已订购的套餐
     *
     * @param username
     */
    private void showMyPackages(String username) {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        OrderDao orderDao = new OrderDaoImpl();
        List<User> users = userDao.getUserByName(username);
        if (users == null || users.size() == 0) {
            System.out.println("用户不存在。");
            double timelen = (System.currentTimeMillis() - time1) / 1000;
            DecimalFormat df = new DecimalFormat("0.000");
            System.out.println("Time: " + df.format(timelen) + "s");
            return;
        }
        for (User user : users) {
            List<Order> orders = orderDao.myOrders(user.getId());
            if (orders == null || orders.size() == 0) {
                System.out.println("此用户尚未订购任何套餐。");
                double timelen = ((System.currentTimeMillis() - time1) / 1000);
                DecimalFormat df = new DecimalFormat("0.000");
                System.out.println("Time: " + df.format(timelen) + "s");
                return;
            }
            System.out.println("客户编号：" + user.getId() + "  客户姓名：" + username);
            for (Order order : orders)
                System.out.println(order.toString());
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 订购套餐
     *
     * @param uid
     * @param pid
     * @param next 表示是否从下个月开始订购。默认false，从本月开始订购
     */
    public void order(int uid, int pid, boolean next) {
        double time1 = System.currentTimeMillis();

        OrderDao orderDao = new OrderDaoImpl();
        if (next) {
            orderDao.subscribeNextMonth(uid, pid);
        } else {
            orderDao.subscribeNow(uid, pid);
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * @param uid     客户编号
     * @param minutes 通话时长
     */
    public void call(int uid, double minutes) {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        userDao.call(uid, minutes);

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 发送短信
     *
     * @param uid
     */
    public void sendMsg(int uid) {
        double time1 = System.currentTimeMillis();

        UserDao userDao = new UserDaoImpl();
        userDao.sendMsg(uid);

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 使用流量
     *
     * @param uid
     * @param flow
     * @param location 可以为null，表示在本地
     */
    public void surf(int uid, String flow, String location) {
        double time1 = System.currentTimeMillis();

        double num = Double.parseDouble(flow.substring(0, flow.length() - 1));
        char c = flow.charAt(flow.length() - 1);
        if (c == 'M') {
            num *= 1024;
        } else if (c == 'G') {
            num *= 1024 * 1024;
        } else if (c == 'K') {

        } else {
            System.out.println("Command not found");
            return;
        }
        UserDao userDao = new UserDaoImpl();
        if (location == null) {
            location = userDao.get(uid).getLocation();
        }
        userDao.useFlow(uid, num, location);

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    /**
     * 退订套餐
     *
     * @param uid
     * @param pid
     * @param next 为true时表示从下月开始退订，否则立即退订
     */
    public void unsubscribe(int uid, int pid, boolean next) {
        double time1 = System.currentTimeMillis();

        OrderDao orderDao = new OrderDaoImpl();
        if (next) {
            orderDao.unsubscribeNextMonth(uid, pid);
        } else {
            orderDao.unsubscribeNow(uid, pid);
        }

        double timelen = ((System.currentTimeMillis() - time1) / 1000);
        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println("Time: " + df.format(timelen) + "s");
    }

    public void unsubscribe(String orderline) {
        String[] attrs = orderline.split("\\s+");
        if (attrs.length == 3) {
            unsubscribe(Integer.parseInt(attrs[1]), Integer.parseInt(attrs[2]), false);
        } else if (attrs.length == 4 && attrs[3].equals("next")) {
            unsubscribe(Integer.parseInt(attrs[1]), Integer.parseInt(attrs[2]), true);
        } else {
            System.out.println("Command not found.");
        }
    }

    public void surf(String orderline) {
        String[] attrs = orderline.split("\\s+");
        if (attrs.length == 3) {
            surf(Integer.parseInt(attrs[1]), attrs[2], null);
        } else if (attrs.length == 4) {
            surf(Integer.parseInt(attrs[1]), attrs[2], attrs[3]);
        } else {
            System.out.println("Command not found.");
        }
    }

    public void sendMsg(String orderline) {
        String[] attrs = orderline.split("\\s+");
        if (attrs.length != 2) {
            System.out.println("Command not found");
            return;
        }
        sendMsg(Integer.parseInt(attrs[1]));
    }

    public void call(String orderline) {
        String[] attrs = orderline.split("\\s+");
        if (attrs.length != 3) {
            System.out.println("Command not found");
            return;
        }
        call(Integer.parseInt(attrs[1]), Integer.parseInt(attrs[2]));
    }


    private void order(String orderline) {
        OrderDao orderDao = new OrderDaoImpl();
        String[] attrs = orderline.split("\\s+");
        if (attrs.length < 3)
            System.out.println("Command not found");
        else if (attrs.length == 3)
            order(Integer.parseInt(attrs[1]), Integer.parseInt(attrs[2]), false);
        else if (attrs.length == 4 && attrs[3].equals("next"))
            order(Integer.parseInt(attrs[1]), Integer.parseInt(attrs[2]), true);
        else
            System.out.println("Command not found");
    }

    private void show(String orderline) {
        String[] orders = orderline.split("\\s+");
        if (orders.length == 2) {
            if (orders[1].equals("-u")) {
                showAllUsers();
            } else if (orders[1].equals("-p")) {
                showAllPackages();
            } else {
                System.out.println("Command not found.");
                return;
            }
        } else if (orders.length == 3) {
            if (orders[1].equals("-p")) {
                showMyPackages(Integer.parseInt(orders[2]));
            } else if (orders[1].equals("-b")) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                showMonthBill(Integer.parseInt(orders[2]), cal.get(Calendar.MONTH));
            } else {
                System.out.println("Command not found.");
                return;
            }
        } else if (orders.length == 4) {
            if (orders[1].equals("-p") && orders[2].equals("-n")) {
                showMyPackages(orders[3]);
            } else if (orders[1].equals("-b")) {
                showMonthBill(Integer.parseInt(orders[2]), Integer.parseInt(orders[3]));
            } else {
                System.out.println("Command not found.");
                return;
            }
        } else {
            System.out.println("Command not found.");
        }
    }

    private void createPackage(String orderline) {
        int start = orderline.indexOf("(") + 1;
        int end = 0;
        for (int i = orderline.length() - 1; i >= 0; i--) {
            if (orderline.charAt(i) == ')') {
                end = i;
            }
        }
        String str = orderline.substring(start, end);
        String[] attrs = str.split("\\s*,\\s*+");
        createPackage(attrs[0], (int) str2digit(attrs[1]), (int) str2digit(attrs[2]), (int) str2digit(attrs[3]),
                str2digit(attrs[4]), str2digit(attrs[5]), str2digit(attrs[6]), str2digit(attrs[7]), attrs[8], attrs[9]);
    }

    private double str2digit(String s) {
        if (s == null || s.trim().length() == 0)
            return 0;
        return Double.parseDouble(s);
    }

    private void register(String orderline) {
        int start = orderline.indexOf("(") + 1;
        int end = 0;
        for (int i = orderline.length() - 1; i >= 0; i--) {
            if (orderline.charAt(i) == ')') {
                end = i;
            }
        }
        String str = orderline.substring(start, end);
        String[] attrs = str.split("\\s*,\\s*+");
        if (attrs.length != 4) {
            System.out.println("Command not found.");
        } else {
            register(attrs[0], Double.parseDouble(attrs[1]), attrs[2], attrs[3]);
        }
    }

}
