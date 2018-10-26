package com.mobile.domain;

import com.mobile.util.TimeLen;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date:2018/10/26
 * author: fu jia xing 161250025
 * 套餐包
 */
public class Package {
    private int pid;
    private String pname;
    private int message_nums;
    private int call_nums;
    private int flow_nums;
    private double pay;
    private boolean valid;
    double call_over_price;
    double msg_over_price;
    double flow_over_price;
    TimeLen timeLen;
    String location = "";

    public Package(int pid, String pname, int message_nums, int call_nums, int flow_nums, double pay, boolean valid,
                   double cop, double mop, double fop, String location,String timelen) {
        this.location = location;
        this.call_over_price = cop;
        this.msg_over_price = mop;
        this.flow_over_price = fop;
        this.pid = pid;
        this.pname = pname;
        this.message_nums = message_nums;
        this.call_nums = call_nums;
        this.flow_nums = flow_nums;
        this.pay = pay;
        this.valid = valid;
        this.timeLen = new TimeLen(timelen);
    }

    public int getPid() {
        return pid;
    }

    public String getLocation() {
        return location;
    }

    public String getPname() {
        return pname;
    }


    public double getPay() {
        return pay;
    }

    public int getFlow_nums() {
        return flow_nums;
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

    public double getCall_over_price() {
        return call_over_price;
    }

    public double getFlow_over_price() {
        return flow_over_price;
    }

    public double getMsg_over_price() {
        return msg_over_price;
    }

    public int getMessage_nums() {
        return message_nums;
    }

    public int getCall_nums() {
        return call_nums;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("");
        res.append("套餐编号：" + this.pid + "  ");
        res.append("套餐类型：" + this.pname + "  月服务费：" + this.pay + "  ");
        if (this.message_nums != 0) {
            res.append("短信条数：" + this.message_nums + "条  ");
        }
        if (this.call_nums != 0) {
            res.append("电话时长：" + this.call_nums + "分钟  ");
        }
        if (this.flow_nums != 0) {
            res.append("流量总数：" + this.flowToString() + "  ");
        }
        //  SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        res.append("套餐时限：" + timeLen.readFormat());
        return res.toString();
    }

    public TimeLen getTimeLen() {
        return timeLen;
    }
}
