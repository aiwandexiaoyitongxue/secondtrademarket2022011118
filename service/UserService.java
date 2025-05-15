package com.secondtrade.service;

import com.secondtrade.entity.User;
import java.util.List;

public interface UserService {
    void register(User user);
    User login(String username, String password);

    // 下面这些都要加上
    List<User> getPendingUsers();
    void approveUser(Long id);
    void rejectUser(Long id);
    List<User> getAllUsers();
    void disableUser(Long id);
    void updateUser(User user);
    User getUserById(Long id);
}