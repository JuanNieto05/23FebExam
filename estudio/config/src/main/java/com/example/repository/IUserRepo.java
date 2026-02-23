package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.model.User;

public interface IUserRepo {
    public void save(User user);
    public List<User> findAll();
    public Optional<User> findById(Integer id);
    public boolean update(User updatedUser);
    public boolean delete(Integer id);
}
