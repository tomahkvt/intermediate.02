package com.task.springboot.service;


import java.util.List;

import com.task.springboot.model.User;

public interface UserService {

    User findById(long id);

    User findByName(String name);

    void saveUser(User user);

    void updateUser(User user);

    void deleteUserById(long id);

    List<User> findAllUsers();


}
