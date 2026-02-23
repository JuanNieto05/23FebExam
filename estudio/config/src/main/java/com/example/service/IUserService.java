package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.model.User;

public interface IUserService {
    public void createUser(User user);
    public List<User> getAllUsers();
    public Optional<User> getUserById(Integer id);
    public boolean updateUser(User user);
    public boolean deleteUser(Integer id);
}
