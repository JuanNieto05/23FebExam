package com.example.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.repository.IUserRepo;
import com.example.service.IUserService;


public class UserService implements IUserService {

    private final IUserRepo userRepo;

    public UserService(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // 🔹 CREATE
    @Override
    public void createUser(User user) {
        userRepo.save(user);
    }

    // 🔹 READ ALL
    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 🔹 READ BY ID
    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepo.findById(id);
    }

    // 🔹 UPDATE
    @Override
    public boolean updateUser(User user) {
        return userRepo.update(user);
    }

    // 🔹 DELETE
    @Override
    public boolean deleteUser(Integer id) {
        return userRepo.delete(id);
    }
}
