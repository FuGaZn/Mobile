package com.mobile.dao;

import com.mobile.domain.Bill;

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
