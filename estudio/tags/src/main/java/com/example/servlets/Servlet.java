package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

        List<User> users = userService.getAllUsers();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Gestión de Usuarios</title></head><body>");

        out.println("<h1>Lista de Usuarios</h1>");
        
        // ===== Listar =====
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Nombre</th><th>Username</th><th>Edad</th></tr>");

        for (User u : users) {
            out.println("<tr>");
            out.println("<td>" + u.getId() + "</td>");
            out.println("<td>" + u.getName() + "</td>");
            out.println("<td>" + u.getUserName() + "</td>");
            out.println("<td>" + u.getAge() + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");

        // ===== CREATE =====
        out.println("<h2>Crear Usuario</h2>");
        out.println("<form method='POST' action='users'>");
        out.println("<input type='hidden' name='action' value='create'>");
        out.println("Nombre:<br>");
        out.println("<input type='text' name='name' required><br>");
        out.println("Username:<br>");
        out.println("<input type='text' name='username' required><br>");
        out.println("Edad:<br>");
        out.println("<input type='number' name='age' required><br>");
        out.println("<button type='submit'>Guardar</button>");
        out.println("</form>");

        // ===== UPDATE =====
        out.println("<h2>Editar Usuario por ID</h2>");
        out.println("<form method='POST' action='users'>");
        out.println("<input type='hidden' name='action' value='update'>");
        out.println("ID:<br>");
        out.println("<input type='number' name='id' required><br>");
        out.println("Nuevo Nombre:<br>");
        out.println("<input type='text' name='name' required><br>");
        out.println("Nuevo Username:<br>");
        out.println("<input type='text' name='username' required><br>");
        out.println("Nueva Edad:<br>");
        out.println("<input type='number' name='age' required><br>");
        out.println("<button type='submit'>Actualizar</button>");
        out.println("</form>");

        // ===== DELETE =====
        out.println("<h2>Eliminar Usuario por ID</h2>");
        out.println("<form method='POST' action='users'>");
        out.println("<input type='hidden' name='action' value='delete'>");
        out.println("ID:<br>");
        out.println("<input type='number' name='id' required><br>");
        out.println("<button type='submit'>Eliminar</button>");
        out.println("</form>");

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        IUserService userService = context.getBean(IUserService.class);

        String action = request.getParameter("action");

        if ("create".equals(action)) {

            String name = request.getParameter("name");
            String username = request.getParameter("username");
            int age = Integer.parseInt(request.getParameter("age"));

            User newUser = new User(null, name, username, age);
            userService.createUser(newUser);
        }

        else if ("update".equals(action)) {

            Integer id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String username = request.getParameter("username");
            int age = Integer.parseInt(request.getParameter("age"));

            User updatedUser = new User(id, name, username, age);
            userService.updateUser(updatedUser);
        }

        else if ("delete".equals(action)) {

            Integer id = Integer.parseInt(request.getParameter("id"));
            userService.deleteUser(id);
        }

        response.sendRedirect("users");
    }
}
