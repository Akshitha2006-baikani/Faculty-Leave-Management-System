package com.facultyleave.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.facultyleave.model.Notification;
import com.facultyleave.util.DBConnection;

public class NotificationDAO {

    public List<Notification> getNotificationsByUserId(int userId) {

        List<Notification> notifications =
                new ArrayList<>();

        String sql =
                "SELECT notification_id, user_id, message, " +
                "is_read, created_at " +
                "FROM notifications " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Notification notification =
                            new Notification();

                    notification.setNotificationId(
                            resultSet.getInt("notification_id")
                    );

                    notification.setUserId(
                            resultSet.getInt("user_id")
                    );

                    notification.setMessage(
                            resultSet.getString("message")
                    );

                    notification.setRead(
                            resultSet.getBoolean("is_read")
                    );

                    notification.setCreatedAt(
                            resultSet.getString("created_at")
                    );

                    notifications.add(notification);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return notifications;
    }

    public boolean createNotification(int userId, String message) {

    String sql =
            "INSERT INTO notifications " +
            "(user_id, message) " +
            "VALUES (?, ?)";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, userId);
        statement.setString(2, message);

        int rowsAffected =
                statement.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {

        e.printStackTrace();

        return false;
    }
}

public boolean createNotification(
        Connection connection,
        int userId,
        String message) {

    String sql =
            "INSERT INTO notifications " +
            "(user_id, message) " +
            "VALUES (?, ?)";

    try (PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, userId);
        statement.setString(2, message);

        int rowsAffected =
                statement.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {

        e.printStackTrace();

        return false;
    }
}
public boolean markAsRead(
        int notificationId,
        int userId) {

    String sql =
            "UPDATE notifications " +
            "SET is_read = 1 " +
            "WHERE notification_id = ? " +
            "AND user_id = ?";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, notificationId);
        statement.setInt(2, userId);

        int rowsAffected =
                statement.executeUpdate();

        return rowsAffected > 0;

    } catch (SQLException e) {

        e.printStackTrace();

        return false;
    }
}
}