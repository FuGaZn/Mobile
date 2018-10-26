package com.mobile.dao;

import com.mobile.domain.User;

public interface UserDao {

    public void call(int uid, int timelen);

    public void sendMsg(int uid);

    public void useFlow(int uid, int nums,String location);

    public boolean hasBalance(int uid);

    public void update(User user);

    public User get(int uid);
}
