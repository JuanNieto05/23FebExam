package com.example.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example") 
public class AppConfig {
    /* No necesitas escribir métodos @Bean aquí adentro. 
       La anotación @ComponentScan le dice a Spring que recorra 
       el paquete 'com.example' y registre automáticamente 
       tus clases marcadas con @Service y @Repository.
    */
}