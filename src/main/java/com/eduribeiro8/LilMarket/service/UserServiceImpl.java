package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dao.UserDAO;
import com.eduribeiro8.LilMarket.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void save(User user) {
        userDAO.save(user);
    }

    public User findUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }
}
