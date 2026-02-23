package com.example.context;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.example.config.AppConfig;

public class ContextSingleton {
    private static ContextSingleton instance;
    private final AnnotationConfigApplicationContext context; // Usamos la clase específica directamente
    
    private ContextSingleton() {

        // si fuera con xml:
        //this.context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // Al usar AnnotationConfigApplicationContext, ya le dices que NO use XML
        this.context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
    
    public static synchronized ContextSingleton getInstance() {
        if (instance == null) {
            instance = new ContextSingleton();
        }
        return instance;
    }
    
    public AnnotationConfigApplicationContext getContext() {
        return context;
    }
    
    public void closeContext() {
        // Al ser 'final' y del tipo específico, solo llamamos a close()
        if (this.context != null) {
            this.context.close();
        }
    }

    //Cierra todo
        //public void closeContext() {
        //    // Usar AbstractApplicationContext permite cerrar tanto XML como AnnotationConfig
        //    if (context instanceof AbstractApplicationContext) {
        //        ((AbstractApplicationContext) context).close();
        //    }
        //}
}