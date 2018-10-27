package com.mobile.domain;

import java.math.BigDecimal;

/**
 * date: 2018/10/26
 * author: fu jia xing  161250025
 */
public class Order {
    int oid;
    int pid;
    int uid;
    String pname;
    int message_nums;
    int call_nums;
    int flow_nums;
    String start;
    String end;
    double pay;
    boolean valid;
    double call_over_price;
    double msg_over_price;
    double flow_over_price;
    String location;
    boolean next_month;

    public Order(int oid, int pid, int uid, String pname, int m, int c, int f, String st, String end, double pay, boolean valid,
                 double cop, double mop, double fop,String location,boolean next_month) {
        this.location=location;
        this.call_over_price = cop;
        this.msg_over_price = mop;
        this.flow_over_price = fop;
        this.oid = oid;
        this.pid = pid;
        this.uid = uid;
        this.pname = pname;
        this.message_nums = m;
        this.call_nums = c;
        this.flow_nums = f;
        this.start = st;
        this.end = end;
        this.valid = valid;
        this.next_month = next_month;
        this.pay = pay;
    }

    public boolean isNextMonthValid() {
        return next_month;
    }

    public int getUid() {
        return uid;
    }

    public double getPay() {
        return pay;
    }

    public String getPname() {
        return pname;
    }

    public String getLocation() {
        return location;
    }

    public String flowToString() {
        String res = "";
        int flow = this.flow_nums;
        if (flow < 1024) {
            res = this.flow_nums + "K";
        } else if (flow < 1048576) {
            res = new BigDecimal(flow / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "M";
        } else
            res = new BigDecimal(flow / 1048576).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "G";
        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("");
        if (this.valid)
            res.append("【使用中】 ");
        else
            res.append("【已失效】 ");
        res.append("套餐编号：" + this.pid + "  ");
        res.append("套餐类型：" + this.pname + "  月功能费：" + this.pay + "  ");
        if (this.message_nums != 0) {
            res.append("剩余短信条数：" + this.message_nums + "条  ");
        }
        if (this.call_nums != 0) {
            res.append("剩余电话时长：" + this.call_nums + "分钟  ");
        }
        if (this.flow_nums != 0) {
            res.append("剩余流量总数：" + this.flowToString() + "  ");
        }
        //  SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        res.append("开始时间：" + (start) + "  结束时间：" + (end));
        return res.toString();
    }

    public int getPid() {
        return pid;
    }

    public int getOid() {
        return oid;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public boolean isValid() {
        return valid;
    }

    public int getMessage_nums() {
        return message_nums;
    }

    public int getCall_nums() {
        return call_nums;
    }

    public double getCall_over_price() {
        return call_over_price;
    }

    public double getFlow_over_price() {
        return flow_over_price;
    }

    public double getMsg_over_price() {
        return msg_over_price;
    }


    public int getFlow_nums() {
        return flow_nums;
    }

    public void setFlow_nums(int flow_nums) {
        this.flow_nums = flow_nums;
    }

    public void setCall_nums(int call_nums) {
        this.call_nums = call_nums;
    }

    public void setMessage_nums(int message_nums) {
        this.message_nums = message_nums;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
