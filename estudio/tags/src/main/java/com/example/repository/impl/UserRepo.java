package com.example.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.model.User;
import com.example.repository.IUserRepo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Repository
public class UserRepo implements IUserRepo{

    private List<User> users = new ArrayList<>();
    private Integer counter = 1;

    // 🔹 CREATE
    @Override
    public void save(User user) {
        user.setId(counter++);
        users.add(user);
    }

    // 🔹 READ ALL
    @Override
    public List<User> findAll() {
        return users;
    }

    // 🔹 READ BY ID
    @Override
    public Optional<User> findById(Integer id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    // 🔹 UPDATE
    @Override
    public boolean update(User updatedUser) {
        Optional<User> optionalUser = findById(updatedUser.getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(updatedUser.getName());
            user.setUserName(updatedUser.getUserName());
            user.setAge(updatedUser.getAge());
            return true;
        }
        return false;
    }

    // 🔹 DELETE
    @Override
    public boolean delete(Integer id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    // 🔹 INIT (Spring lifecycle)
    @PostConstruct
    public void init() {
        save(new User(null, "jose", "jose123", 20));
    }

    @PreDestroy
    public void destroy() {
        users.clear();
    }
}

