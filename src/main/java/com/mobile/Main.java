package com.mobile;

import com.mobile.dao.PackageDao;
import com.mobile.dao.UserDao;
import com.mobile.dao.impl.PackageDaoImpl;
import com.mobile.dao.impl.UserDaoImpl;
import com.mobile.domain.Package;
import com.mobile.domain.User;
import com.mobile.util.TimeLen;

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
                    break;

                /**-----------------------------------------------
                 * 用户订购套餐操作：
                 * 命令：order uid pid
                 * uid为客户编号
                 * pid为套餐编号
                 *
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "order":
                    break;

                /**-----------------------------------------------
                 * 退订套餐操作：
                 * 命令：unsub uid pid
                 *
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "unsub":
                    break;

                /**-----------------------------------------------
                 * 用户拨打电话时的资费生成：
                 * 命令：call uid minutes
                 * uid  客户编号
                 * minutes  拨打电话的时长
                 * 返回信息：使用的套餐、扣除的费用、余额
                 *
                 * 实例：call 1 1.5
                 * 详情见说明文档。
                 * -----------------------------------------------
                 */
                case "call":
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

        User user = new User(0, name, balance, location, phone);
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

    private void showMonthBill(int uid, int month){

    }

    private void show(String orderline){

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
            System.out.println("用户信息错误。");
        } else {
            register(attrs[0], Double.parseDouble(attrs[1]), attrs[2], attrs[3]);
        }
    }

}
