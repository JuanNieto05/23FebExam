package com.example;

import java.io.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.model.User;
import com.example.service.UserService;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.List;




@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;
    private UserService userService;
    
    public void init() {
          ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
          this.userService = (UserService) context.getBean("UserService");  
     }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

     List<User> users = userService.getAllUsers();
     if (users == null) users = new java.util.ArrayList<>();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Lista de usuarios</h1>");

        if (users.isEmpty()) {
            out.println("<p>No hay usuarios aún</p>");
        } else {
            for (User user : users) {
                out.println("<p>" + user.getId() + " - " + user.getName() + " (" + user.getUsername() + ")</p>");
            }
        }

        out.println("</body></html>");

     

    List<User> user = userService.getAllUsers();

    response.setContentType("text/html");
    PrintWriter out1 = response.getWriter();

    out1.println("<html><body>");
    out1.println("<h1>Lista de usuarios</h1>");

    // todo para crear los usuarios
    out1.println("<h3>Crear usuario</h3>");
    out1.println("<form method='get' action='hello-servlet'>");
    out1.println("<input type='hidden' name='action' value='create'/>");
    out1.println("Nombre: <input name='name'/> <br/>");
    out1.println("Username: <input name='username'/> <br/>");
    out1.println("Edad: <input name='age' type='number'/> <br/>");
    out1.println("<button type='submit'>Crear</button>");
    out1.println("</form>");
    out1.println("<hr/>");

    // Lista + botones borrar
    if (users.isEmpty()) {
        out.println("<p>No hay usuarios aún</p>");
    } else {
        for (User u : user) {
            out.println("<p>");
            out.println(u.getId() + " - " + u.getName() + " (" + u.getUsername() + ") ");
            out.println("<a href='hello-servlet?action=delete&id=" + u.getId() + "'>Eliminar</a>");
            out.println("</p>");
        }
    }

    out.println("</body></html>");
}


    public void destroy() {
     System.out.println("Servlet destruido");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException ,IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        userService.createUser(name, age, username);
        response.sendRedirect("hello-servlet"); 
        return;
    }

    if ("delete".equals(action)) {
        int id = Integer.parseInt(request.getParameter("id"));
        userService.deleteUserById(id);
        response.sendRedirect("hello-servlet");
        return;
    }

    if ("update".equals(action)) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
        userService.updateUser(id, name, username, age);
        response.sendRedirect("hello-servlet");
        return;
    }
    };
}
