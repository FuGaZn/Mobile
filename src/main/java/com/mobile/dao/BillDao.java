package com.mobile.dao;

import com.mobile.domain.Bill;

/**
 * date: 2018/10/28
 * author: fu jia xing  161250025
 */
public interface BillDao {
    /**
     * 新建账单
     *
     * @param bill
     */
    public void add(Bill bill);

    /**
     * 更新账单
     *
     * @param bill
     */
    public void update(Bill bill);

    /**
     * @param uid
     * @param year
     * @param month
     * @return
     */
    public Bill get(int uid, String year, int month);

}
