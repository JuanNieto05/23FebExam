package com.example.model;

public class User {
    private Integer id;
    private String name;
    private String userName;
    private int age;

    public User(Integer id, String name, String userName, int age) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
    
}
