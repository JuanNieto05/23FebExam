package com.example.service;

import java.util.List;

import com.example.repository.UserRepository;
import com.example.model.User;

public class UserService {
    private UserRepository userRepository;
    //Inyeccion por constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //inyeccion por metodo setter
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //  creacion de los usuarios jejej    
    public User createUser(String name, int age, String username){
        if (name == null || name.isEmpty() || username == null || username.isEmpty() || age <= 0) {
            throw new IllegalArgumentException("Invalid user data");
        }
        User user = new User (null,name,username,age);
        return userRepository.saveUser(user);
    }
    // lectura de usuarios
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
    public User getUserById(int id){
        return userRepository.getUserById(id);
    }
    // actualizacion de usuarios
    public User updateUser(Integer id, String name,String username, int age){
        User updated = new User (id,name,username,age);
        return userRepository.update(id,updated);
    
    } 
    // eliminacion de usuarios
    public boolean deleteUserById(int id){
        return userRepository.deleteById(id);
    }

}