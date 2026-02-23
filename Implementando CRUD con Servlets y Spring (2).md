¡Qué buen proyecto\! Al revisar los archivos que subiste, noto algo súper importante: **no estás usando el típico Spring Boot con @RestController**, sino que estás construyendo la aplicación usando **Servlets puros de Java** combinados con la inyección de dependencias de **Spring Core** (AnnotationConfigApplicationContext).

Esta es la mejor forma de aprender, porque ves cómo funciona la "magia" por debajo.

En tu archivo README.md, tienes una tarea pendiente clave: **"Implementar los repositorios y servicios de los roles y los permisos"**.

Vamos a completar el CRUD exactamente para la clase Roles. Te explicaré paso a paso cómo, dónde y **por qué** se hace cada cosa, para que tu Servlet no se convierta en un "Controlador Gordo".

### ---

**Paso 1: La Interfaz del Repositorio (El Contrato)**

**¿Por qué lo hacemos?** Tu README.md dice: *"Inyectar todos los elementos por medio de interfaces para evitar el acoplamiento"*. Si el día de mañana cambias tu base de datos (ej. pasas de listas en memoria a PostgreSQL), el resto del código no se rompe porque solo dependen de esta interfaz.

**Cómo se hace:**

Crea un archivo llamado IRoleRepository.java en com.example.repository:

Java

package com.example.repository;

import java.util.List;  
import java.util.Optional;  
import com.example.model.Roles;

public interface IRoleRepository {  
    List\<Roles\> findAll(); // Leer todos  
    Optional\<Roles\> findById(Integer id); // Leer uno  
    Roles save(Roles role); // Crear o Actualizar  
    boolean delete(Integer id); // Borrar  
}

### **Paso 2: La Implementación del Repositorio (La Bodega)**

**¿Por qué lo hacemos?** Aquí es donde realmente guardamos los datos. Como vi en tu UserRepository, estás usando listas en memoria simulando una base de datos. Usamos la anotación @Repository para que Spring sepa que esta clase maneja datos y la convierta en un *Bean* manejable.

**Cómo se hace:**

Crea RoleRepository.java en com.example.repository.impl:

Java

package com.example.repository.impl;

import java.util.ArrayList;  
import java.util.List;  
import java.util.Optional;  
import org.springframework.stereotype.Repository;  
import com.example.model.Roles;  
import com.example.repository.IRoleRepository;  
import jakarta.annotation.PostConstruct;

@Repository // INDISPENSABLE: Le dice a Spring "Yo soy la base de datos"  
public class RoleRepository implements IRoleRepository {  
      
    private List\<Roles\> roles \= new ArrayList\<\>();  
    private int nextId \= 1;

    // @PostConstruct llena datos de prueba apenas arranca la app  
    @PostConstruct  
    public void init() {  
        save(new Roles("ADMIN", "Administrador del sistema"));  
        save(new Roles("USER", "Usuario regular"));  
    }

    @Override  
    public List\<Roles\> findAll() { return new ArrayList\<\>(roles); }

    @Override  
    public Optional\<Roles\> findById(Integer id) {  
        return roles.stream().filter(r \-\> r.getId().equals(id)).findFirst();  
    }

    @Override  
    public Roles save(Roles role) {  
        // Lógica de Crear (INSERT)  
        if (role.getId() \== null) {  
            role.setId(nextId++);  
            roles.add(role);  
            return role;  
        }  
        // Lógica de Actualizar (UPDATE)  
        for (int i \= 0; i \< roles.size(); i++) {  
            if (role.getId().equals(roles.get(i).getId())) {  
                roles.set(i, role);  
                return role;  
            }  
        }  
        return role;  
    }

    @Override  
    public boolean delete(Integer id) {  
        return roles.removeIf(r \-\> r.getId().equals(id));  
    }  
}

### **Paso 3: El Servicio (El Cerebro)**

**¿Por qué lo hacemos?** El repositorio es tonto, solo guarda y saca cosas. El **Servicio** toma decisiones lógicas. Además, usamos @Service para registrarlo en Spring y @Autowired (o por constructor) para "inyectarle" el repositorio sin tener que hacer new RoleRepository().

**Cómo se hace:**

Crea RoleService.java en com.example.service:

Java

package com.example.service;

import java.util.List;  
import org.springframework.stereotype.Service;  
import com.example.model.Roles;  
import com.example.repository.IRoleRepository;

@Service // INDISPENSABLE: Le dice a Spring "Yo contengo la lógica de negocio"  
public class RoleService {  
      
    private final IRoleRepository roleRepository;

    // Inyección de dependencias por constructor (es la forma más segura)  
    public RoleService(IRoleRepository roleRepository) {  
        this.roleRepository \= roleRepository;  
    }

    public List\<Roles\> findAll() {  
        return roleRepository.findAll();  
    }

    public Roles findById(Integer id) {  
        return roleRepository.findById(id)  
                .orElseThrow(() \-\> new IllegalArgumentException("Rol no encontrado"));  
    }

    public Roles save(Roles role) {  
        // Aquí podrías agregar lógica extra, ej: "Si el nombre está vacío, rechazar"  
        return roleRepository.save(role);  
    }  
}

### **Paso 4: El Servlet (El Recepcionista / Controlador)**

**¿Por qué lo hacemos?** Porque en tu arquitectura no usas @RestController. Los Servlets capturan las peticiones web (GET, POST) que vienen del navegador. Aquí el Servlet **solo extrae parámetros de la URL** y **se los pasa al Servicio**. ¡No toma decisiones\!

**Cómo se hace:**

Siguiendo la estructura de tu GamesServlet, crea RolesServlet.java en com.example.servlets:

Java

package com.example.servlets;

import java.io.IOException;  
import org.springframework.context.ApplicationContext;  
import com.example.context.ContextSingleton;  
import com.example.model.Roles;  
import com.example.service.RoleService;  
import jakarta.servlet.annotation.WebServlet;  
import jakarta.servlet.http.HttpServlet;  
import jakarta.servlet.http.HttpServletRequest;  
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name \= "rolesServlet", value \= "/roles")  
public class RolesServlet extends HttpServlet {

    private ApplicationContext context;

    @Override  
    public void init() {  
        // Así recuperamos el contexto de Spring en tu proyecto específico  
        this.context \= ContextSingleton.getInstance().getContext();   
    }

    // \--- READ (LEER) \---  
    @Override  
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {  
        RoleService roleService \= context.getBean(RoleService.class);  
          
        response.setContentType("text/html");  
        response.getWriter().println("\<h1\>Lista de Roles\</h1\>\<ul\>");  
          
        // Llamamos al servicio para obtener los datos  
        roleService.findAll().forEach(role \-\> {  
            try {  
                response.getWriter().println("\<li\>" \+ role.getName() \+ " \- " \+ role.getDescription() \+ "\</li\>");  
            } catch (IOException e) { e.printStackTrace(); }  
        });  
          
        response.getWriter().println("\</ul\>");  
          
        // Formulario para Crear un Rol (POST)  
        response.getWriter().println("\<h2\>Crear Rol\</h2\>");  
        response.getWriter().println("\<form method='post' action='roles'\>");  
        response.getWriter().println("Nombre: \<input type='text' name='name'\>\<br\>");  
        response.getWriter().println("Descripción: \<input type='text' name='description'\>\<br\>");  
        response.getWriter().println("\<input type='submit' value='Guardar Rol'\>");  
        response.getWriter().println("\</form\>");  
    }

    // \--- CREATE (CREAR) \---  
    @Override  
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {  
        String name \= request.getParameter("name");  
        String description \= request.getParameter("description");

        RoleService roleService \= context.getBean(RoleService.class);

        // Creamos el objeto y usamos el servicio para guardarlo  
        Roles nuevoRol \= new Roles(name, description);  
        roleService.save(nuevoRol);

        // Redirigimos a la misma página para ver la lista actualizada  
        response.sendRedirect("roles");  
    }  
}

### **Resumen del Flujo (El Porqué general)**

Si en el quiz te dicen *"Crea la funcionalidad para agregar un Rol"*:

1. El usuario llena el formulario HTML y da clic en submit (Petición HTTP POST).  
2. El **RolesServlet** (doPost) captura esos textos (name, description), crea un objeto y llama al RoleService.  
3. El **RoleService** recibe la orden. Podría verificar que el nombre no esté repetido, y si todo está bien, llama a IRoleRepository.save().  
4. El **RoleRepository** le asigna un ID, lo guarda en su Lista de memoria, y lo devuelve.

Con este flujo, cumples con las buenas prácticas, evitas el acoplamiento y resuelves casi todo lo que te pide el README.md. Puedes replicar este mismo modelo exacto para la clase Permissions.