package com.facultyleave.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.facultyleave.model.User;
import com.facultyleave.util.DBConnection;

public class UserDAO {

    // Register a new user
    public boolean registerUser(User user) {

        String sql = "INSERT INTO users "
                   + "(name, email, password, role, department_id) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());

            if (user.getDepartmentId() != null) {
                statement.setInt(5, user.getDepartmentId());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find a user by email
    public User findByEmail(String email) {

        String sql = "SELECT user_id, name, email, password, role, department_id "
                   + "FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return createUserFromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Find a user by ID
    public User findById(int userId) {

        String sql = "SELECT user_id, name, email, password, role, department_id "
                   + "FROM users WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return createUserFromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Check whether an email already exists
    public boolean emailExists(String email) {

        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Register a faculty user and initialize leave balances
    public boolean registerFacultyWithBalance(User user) {

        String insertUserSql =
                "INSERT INTO users " +
                "(name, email, password, role, department_id) " +
                "VALUES (?, ?, ?, 'FACULTY', ?)";

        String insertBalanceSql =
                "INSERT INTO leave_balance " +
                "(user_id, leave_type_id, remaining_days) " +
                "SELECT ?, leave_type_id, total_days " +
                "FROM leave_types";

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // Start transaction
            connection.setAutoCommit(false);

            int userId;

            // 1. Create faculty account
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 insertUserSql,
                                 java.sql.Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.setInt(4, user.getDepartmentId());

                int rows = statement.executeUpdate();

                if (rows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }

                    userId = generatedKeys.getInt(1);
                    user.setUserId(userId);
                }
            }

            // 2. Initialize all leave balances
            try (PreparedStatement statement =
                         connection.prepareStatement(insertBalanceSql)) {

                statement.setInt(1, userId);

                int balanceRows = statement.executeUpdate();

                if (balanceRows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // 3. Everything succeeded
            connection.commit();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            return false;

        } finally {

            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Convert database result into User object
    private User createUserFromResultSet(ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setUserId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(resultSet.getString("role"));

        int departmentId = resultSet.getInt("department_id");

        if (resultSet.wasNull()) {
            user.setDepartmentId(null);
        } else {
            user.setDepartmentId(departmentId);
        }

        return user;
    }
}