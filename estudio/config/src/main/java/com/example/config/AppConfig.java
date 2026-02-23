package com.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.repository.IUserRepo;
import com.example.service.IUserService;
import com.example.repository.impl.UserRepo;
import com.example.service.impl.UserService;

@Configuration
public class AppConfig {

    // 1. Definimos el Repositorio
    // initMethod y destroyMethod reemplazan a @PostConstruct y @PreDestroy
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public IUserRepo userRepo() {
        return new UserRepo();
    }

    // 2. Definimos el Servicio e inyectamos el Repo manualmente
    @Bean
    public IUserService userService() {
        // Aquí pasamos la instancia creada arriba
        return new UserService(userRepo());
    }
}