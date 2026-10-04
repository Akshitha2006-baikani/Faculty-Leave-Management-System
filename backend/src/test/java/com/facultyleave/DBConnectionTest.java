package com.facultyleave;

import java.sql.Connection;

import com.facultyleave.util.DBConnection;

public class DBConnectionTest {

    public static void main(String[] args) {

        try {
            Connection connection = DBConnection.getConnection();

            if (connection != null) {
                System.out.println("=================================");
                System.out.println("DATABASE CONNECTION SUCCESSFUL!");
                System.out.println("Database: faculty_leave_management");
                System.out.println("=================================");

                connection.close();
            }

        } catch (Exception e) {
            System.out.println("=================================");
            System.out.println("DATABASE CONNECTION FAILED!");
            System.out.println("=================================");
            e.printStackTrace();
        }
    }
}