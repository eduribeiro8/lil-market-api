package com.eduribeiro8.LilMarket.dao;

import com.eduribeiro8.LilMarket.entity.User;

public interface UserDAO {

    void save(User user);

    User findUserById(User user);

    boolean login(User user);
}
