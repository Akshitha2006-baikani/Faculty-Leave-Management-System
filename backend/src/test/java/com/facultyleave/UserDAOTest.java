package com.facultyleave;

import com.facultyleave.dao.UserDAO;
import com.facultyleave.model.User;

public class UserDAOTest {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        String email = "testfaculty@gmail.com";

        System.out.println("=================================");
        System.out.println("TESTING USER DAO");
        System.out.println("=================================");

        // Check whether test email already exists
        if (userDAO.emailExists(email)) {

            System.out.println("Test user already exists.");
            System.out.println("Finding user by email...");

            User user = userDAO.findByEmail(email);

            if (user != null) {
                System.out.println("User found successfully!");
                System.out.println("User ID: " + user.getUserId());
                System.out.println("Name: " + user.getName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Role: " + user.getRole());
                System.out.println("Department ID: " + user.getDepartmentId());
            }

        } else {

            User user = new User(
                    "Test Faculty",
                    email,
                    "test123",
                    "FACULTY",
                    null
            );

            boolean registered = userDAO.registerUser(user);

            if (registered) {
                System.out.println("User registration successful!");

                User savedUser = userDAO.findByEmail(email);

                if (savedUser != null) {
                    System.out.println("User retrieved successfully!");
                    System.out.println("User ID: " + savedUser.getUserId());
                    System.out.println("Name: " + savedUser.getName());
                    System.out.println("Email: " + savedUser.getEmail());
                    System.out.println("Role: " + savedUser.getRole());
                    System.out.println("Department ID: "
                            + savedUser.getDepartmentId());
                }

            } else {
                System.out.println("User registration FAILED!");
            }
        }

        System.out.println("=================================");
        System.out.println("DAO TEST COMPLETED");
        System.out.println("=================================");
    }
}