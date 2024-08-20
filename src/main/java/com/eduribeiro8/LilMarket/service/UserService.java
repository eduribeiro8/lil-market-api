package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.entity.User;

public interface UserService {

    void save(User user);

    User findUserByUsername(String username);
}
