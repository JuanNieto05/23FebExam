package com.example.repository;

import java.util.ArrayList;
import java.util.List;


import com.example.model.User;

public class UserRepository {
    private List<User> users = new ArrayList<User>();
    private Integer idCounter = 1;

    public User saveUser(User user) {
        user.setId(idCounter++);
        users.add(user);
        return user;
    }
    public List<User> getAllUsers() {
        return users;
    }
    public User getUserById(int id){
        for (User user : users){
            if (user.getId()==id ){
                return user;
            }

        }
        return null;
    }
    public User update(int id, User updateUser) {
        User existing = getUserById(id);
        if (existing == null) return null;
        existing.setName(updateUser.getName());
        existing.setUsername(updateUser.getUsername());
        existing.setAge(updateUser.getAge());
        return existing;
    }

    public Boolean deleteById(int id){
        return users.removeIf(user -> user.getId() == id);
    }

    public void destroy(){
        users.clear();
    }
}
