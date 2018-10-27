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
    boolean next_month;

    public Package(int pid, String pname, int message_nums, int call_nums, int flow_nums, double pay, boolean valid,
                   double mop, double cop, double fop, String location,String timelen,boolean next) {
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
        this.next_month = next;
    }

    public int getPid() {
        return pid;
    }

    public boolean isNextMonthValid() {
        return next_month;
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
        res.append("套餐名称：" + this.pname + "  月功能费：" + this.pay + "  ");
        if (this.message_nums != 0) {
            res.append("最多可发送短信条数：" + this.message_nums + "条  ");
        }
        if (this.msg_over_price>0)
            res.append("超出条数按照"+this.msg_over_price+"元/条计费  ");

        if (this.call_nums != 0) {
            res.append("最多可拨打电话时长：" + this.call_nums + "分钟  ");
        }
        if (this.call_over_price>0)
            res.append("超出时间按照"+this.call_over_price+"元/分钟计费  ");

        if (this.flow_nums != 0) {
            res.append("最多可获得流量总数：" + this.flowToString() + "  ");
        }
        if (this.flow_over_price>0)
            res.append("超出流量按照"+this.flow_over_price+"元/M计费  ");

        if(this.location!=null || this.location.length()>0)
            res.append("归属地："+this.location+"  ");
        //  SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        res.append("套餐时限：" + timeLen.readFormat());
        return res.toString();
    }

    public TimeLen getTimeLen() {
        return timeLen;
    }
}
