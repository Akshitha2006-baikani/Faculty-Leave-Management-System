package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.UserDAO;
import com.facultyleave.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic validation
        if (email == null || email.trim().isEmpty()
                || password == null || password.isEmpty()) {

            response.sendRedirect("login.html?error=empty");
            return;
        }

        email = email.trim();

        // Find user by email
        User user = userDAO.findByEmail(email);

        // Check credentials
        if (user != null && user.getPassword().equals(password)) {

            HttpSession session = request.getSession();

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userDesignation", user.getDesignation());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("departmentId", user.getDepartmentId());
            session.setAttribute("departmentName", user.getDepartmentName());

            // Redirect based on role
            if ("FACULTY".equals(user.getRole())) {

                response.sendRedirect("faculty-dashboard.html");

            } else if ("HOD".equals(user.getRole())) {

                response.sendRedirect("hod-dashboard.jsp");

            } else if ("ADMIN".equals(user.getRole())) {

                response.sendRedirect("admin-dashboard.html");

            } else {

                response.sendRedirect("login.html?error=role");
            }

        } else {

            response.sendRedirect("login.html?error=invalid");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("login.html");
    }
}