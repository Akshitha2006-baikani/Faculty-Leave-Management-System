package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facultyleave.dao.UserDAO;
import com.facultyleave.model.User;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Read form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String departmentIdString = request.getParameter("departmentId");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Remove unnecessary spaces
        if (name != null) {
            name = name.trim();
        }

        if (email != null) {
            email = email.trim();
        }

        // Validate required fields
        if (name == null || name.isEmpty()
                || email == null || email.isEmpty()
                || departmentIdString == null || departmentIdString.isEmpty()
                || password == null || password.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {

            request.setAttribute("error",
                    "Please fill in all required fields.");

            request.getRequestDispatcher("register.html")
                    .forward(request, response);

            return;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {

            request.setAttribute("error",
                    "Passwords do not match.");

            request.getRequestDispatcher("register.html")
                    .forward(request, response);

            return;
        }

        // Convert department ID
        int departmentId;

        try {
            departmentId = Integer.parseInt(departmentIdString);

        } catch (NumberFormatException e) {

            request.setAttribute("error",
                    "Please select a valid department.");

            request.getRequestDispatcher("register.html")
                    .forward(request, response);

            return;
        }

        // Check whether email already exists
        if (userDAO.emailExists(email)) {

            request.setAttribute("error",
                    "An account with this email already exists.");

            request.getRequestDispatcher("register.html")
                    .forward(request, response);

            return;
        }

        // Create User object
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // IMPORTANT:
        // Public registration always creates FACULTY accounts.
        user.setRole("FACULTY");

        user.setDepartmentId(departmentId);

        // Save user + initialize leave balances
        boolean registered =
                userDAO.registerFacultyWithBalance(user);

        if (registered) {

            // Registration successful
            response.sendRedirect(
                    "login.html?registered=true");

        } else {

            request.setAttribute("error",
                    "Registration failed. Please try again.");

            request.getRequestDispatcher("register.html")
                    .forward(request, response);
        }
    }
}


