package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.model.User;
import com.example.service.IUserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "userServlet", value = "/users")
public class Servlet extends HttpServlet {

    private ApplicationContext context;

    @Override
    public void init() throws ServletException {

        // Conectamos con el Singleton al iniciar el Servlet
        this.context = ContextSingleton.getInstance().getContext();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        IUserService userService = context.getBean(IUserService.class);
        String action = request.getParameter("action");

        // ===== DELETE =====
        if ("delete".equals(action)) {
            Integer id = Integer.parseInt(request.getParameter("id"));
            userService.deleteUser(id);
            response.sendRedirect("users");
            return;
        }

        // ===== EDIT =====
        if ("edit".equals(action)) {
            Integer id = Integer.parseInt(request.getParameter("id"));
            Optional<User> optionalUser = userService.getUserById(id);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();

                out.println("<html><body>");
                out.println("<h2>Editar Usuario</h2>");
                out.println("<form method='POST' action='users'>");
                out.println("<input type='hidden' name='id' value='" + user.getId() + "'>");
                out.println("Nombre: <input type='text' name='name' value='" + user.getName() + "'><br>");
                out.println("Username: <input type='text' name='username' value='" + user.getUserName() + "'><br>");
                out.println("Edad: <input type='number' name='age' value='" + user.getAge() + "'><br>");
                out.println("<input type='submit' value='Actualizar Usuario'>");
                out.println("</form>");
                out.println("</body></html>");
            }
            return;
        }

        // ===== LIST =====
        List<User> users = userService.getAllUsers();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Gestión de Usuarios</title></head><body>");
        out.println("<h1>Lista de Usuarios</h1>");

        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Nombre</th><th>Username</th><th>Edad</th><th>Acciones</th></tr>");

        for (User u : users) {
            out.println("<tr>");
            out.println("<td>" + u.getId() + "</td>");
            out.println("<td>" + u.getName() + "</td>");
            out.println("<td>" + u.getUserName() + "</td>");
            out.println("<td>" + u.getAge() + "</td>");
            out.println("<td>");
            out.println("<a href='users?action=edit&id=" + u.getId() + "'>Editar</a> ");
            out.println("<a href='users?action=delete&id=" + u.getId() + "' onclick='return confirm(\"¿Seguro?\")'>Eliminar</a>");
            out.println("</td>");
            out.println("</tr>");
        }

        out.println("</table>");

        // FORMULARIO CREATE
        out.println("<h2>Agregar Nuevo Usuario</h2>");
        out.println("<form method='POST' action='users'>");
        out.println("Nombre: <input type='text' name='name'><br>");
        out.println("Username: <input type='text' name='username'><br>");
        out.println("Edad: <input type='number' name='age'><br>");
        out.println("<input type='submit' value='Guardar Usuario'>");
        out.println("</form>");

        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
            
        IUserService userService = context.getBean(IUserService.class);
            
        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));
            
        // ===== CREATE =====
        if (idParam == null || idParam.isEmpty()) {
            User newUser = new User(null, name, username, age);
            userService.createUser(newUser);
        }
        // ===== UPDATE =====
        else {
            Integer id = Integer.parseInt(idParam);
            User updatedUser = new User(id, name, username, age);
            userService.updateUser(updatedUser);
        }
    
        response.sendRedirect("users");
    }
}
