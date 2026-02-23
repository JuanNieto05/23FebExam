package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.service.UserService;

import com.example.beans.BeanA;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
        BeanA beanA = (BeanA) context.getBean("beanA"); 
        System.out.println(beanA.getMessage());
        BeanA beanB = (BeanA) context.getBean("beanA");
        UserService userService = (UserService) context.getBean("UserService");
        System.out.println(beanA == beanB);

    }
    
}
