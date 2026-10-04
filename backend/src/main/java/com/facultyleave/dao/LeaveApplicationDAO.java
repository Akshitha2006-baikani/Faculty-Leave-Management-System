package com.facultyleave.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.facultyleave.model.LeaveApplication;
import com.facultyleave.util.DBConnection;
public class LeaveApplicationDAO {
    private final NotificationDAO notificationDAO = new NotificationDAO();
    public boolean applyLeave(
            int userId,
            int leaveTypeId,
            Date startDate,
            Date endDate,
            String reason) {

        long difference =
                endDate.getTime() - startDate.getTime();

        int numberOfDays =
                (int) (difference / (1000 * 60 * 60 * 24)) + 1;

        if (numberOfDays <= 0) {
            return false;
        }

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Check the faculty member's current leave balance.
             */
            String balanceSql =
                    "SELECT remaining_days " +
                    "FROM leave_balance " +
                    "WHERE user_id = ? " +
                    "AND leave_type_id = ? " +
                    "FOR UPDATE";

            int remainingDays;

            try (PreparedStatement statement =
                         connection.prepareStatement(balanceSql)) {

                statement.setInt(1, userId);
                statement.setInt(2, leaveTypeId);

                try (ResultSet resultSet =
                             statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    remainingDays =
                            resultSet.getInt("remaining_days");
                }
            }

            /*
             * Do not allow the faculty member to apply
             * for more days than the available balance.
             */
            if (remainingDays < numberOfDays) {
                connection.rollback();
                return false;
            }

            /*
             * Insert the leave application.
             */
            String applicationSql =
                    "INSERT INTO leave_applications " +
                    "(user_id, leave_type_id, start_date, " +
                    "end_date, reason) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement =
                         connection.prepareStatement(applicationSql)) {

                statement.setInt(1, userId);
                statement.setInt(2, leaveTypeId);
                statement.setDate(3, startDate);
                statement.setDate(4, endDate);
                statement.setString(5, reason);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            /*
             * Reduce the leave balance.
             */
            String updateBalanceSql =
                    "UPDATE leave_balance " +
                    "SET remaining_days = remaining_days - ? " +
                    "WHERE user_id = ? " +
                    "AND leave_type_id = ?";

            try (PreparedStatement statement =
                         connection.prepareStatement(updateBalanceSql)) {

                statement.setInt(1, numberOfDays);
                statement.setInt(2, userId);
                statement.setInt(3, leaveTypeId);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            String notificationMessage =
        "Your leave application has been submitted successfully.";

boolean notificationCreated =
        notificationDAO.createNotification(
                connection,
                userId,
                notificationMessage
        );

if (!notificationCreated) {
    connection.rollback();
    return false;
}

connection.commit();

return true;

        } catch (SQLException e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

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


    /*
     * Get all leave applications submitted by one faculty member
     */
    public List<LeaveApplication> getApplicationsByUserId(int userId) {

        List<LeaveApplication> applications =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "la.leave_id, "
                + "la.user_id, "
                + "lt.leave_name, "
                + "la.start_date, "
                + "la.end_date, "
                + "la.reason, "
                + "la.status, "
                + "la.hod_remarks, "
                + "la.applied_date "
                + "FROM leave_applications la "
                + "JOIN leave_types lt "
                + "ON la.leave_type_id = lt.leave_type_id "
                + "WHERE la.user_id = ? "
                + "ORDER BY la.applied_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    LeaveApplication application =
                            new LeaveApplication();

                    application.setLeaveId(
                            resultSet.getInt("leave_id")
                    );

                    application.setUserId(
                            resultSet.getInt("user_id")
                    );

                    application.setLeaveType(
                            resultSet.getString("leave_name")
                    );

                    application.setStartDate(
                            resultSet.getString("start_date")
                    );

                    application.setEndDate(
                            resultSet.getString("end_date")
                    );

                    application.setReason(
                            resultSet.getString("reason")
                    );

                    application.setStatus(
                            resultSet.getString("status")
                    );

                    application.setHodRemarks(
                            resultSet.getString("hod_remarks")
                    );

                    application.setAppliedDate(
                            resultSet.getString("applied_date")
                    );

                    applications.add(application);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return applications;
    }


    /*
     * Cancel a pending leave application.
     *
     * The cancelled application remains in history.
     * The number of leave days is restored to the balance.
     */
    public boolean cancelLeave(int leaveId, int userId) {

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Get the leave application details and lock the row.
             */
            String selectSql =
                    "SELECT leave_type_id, start_date, end_date " +
                    "FROM leave_applications " +
                    "WHERE leave_id = ? " +
                    "AND user_id = ? " +
                    "AND status = 'PENDING' " +
                    "FOR UPDATE";

            int leaveTypeId;
            Date startDate;
            Date endDate;

            try (PreparedStatement statement =
                         connection.prepareStatement(selectSql)) {

                statement.setInt(1, leaveId);
                statement.setInt(2, userId);

                try (ResultSet resultSet =
                             statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    leaveTypeId =
                            resultSet.getInt("leave_type_id");

                    startDate =
                            resultSet.getDate("start_date");

                    endDate =
                            resultSet.getDate("end_date");
                }
            }

            /*
             * Calculate the number of leave days.
             * Both start and end dates are included.
             */
            long difference =
                    endDate.getTime() - startDate.getTime();

            int numberOfDays =
                    (int) (difference / (1000 * 60 * 60 * 24)) + 1;

            /*
             * Change the application status to CANCELLED.
             */
            String cancelSql =
                    "UPDATE leave_applications " +
                    "SET status = 'CANCELLED' " +
                    "WHERE leave_id = ? " +
                    "AND user_id = ? " +
                    "AND status = 'PENDING'";

            try (PreparedStatement statement =
                         connection.prepareStatement(cancelSql)) {

                statement.setInt(1, leaveId);
                statement.setInt(2, userId);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            /*
             * Restore the cancelled leave days.
             */
            String restoreBalanceSql =
                    "UPDATE leave_balance " +
                    "SET remaining_days = remaining_days + ? " +
                    "WHERE user_id = ? " +
                    "AND leave_type_id = ?";

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 restoreBalanceSql)) {

                statement.setInt(1, numberOfDays);
                statement.setInt(2, userId);
                statement.setInt(3, leaveTypeId);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            String notificationMessage =
        "Your leave application has been cancelled successfully.";

boolean notificationCreated =
        notificationDAO.createNotification(
                connection,
                userId,
                notificationMessage
        );

if (!notificationCreated) {
    connection.rollback();
    return false;
}

connection.commit();

return true;

        } catch (SQLException e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

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
}