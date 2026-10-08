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

    private final NotificationDAO notificationDAO =
            new NotificationDAO();

    /*
     * Apply for leave.
     *
     * The requested leave days are deducted immediately
     * from the faculty member's balance.
     */
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
             * Check current leave balance.
             */
            String balanceSql =
                    "SELECT remaining_days "
                    + "FROM leave_balance "
                    + "WHERE user_id = ? "
                    + "AND leave_type_id = ? "
                    + "FOR UPDATE";

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
             * Do not allow application when balance is insufficient.
             */
            if (remainingDays < numberOfDays) {
                connection.rollback();
                return false;
            }

            /*
             * Insert leave application.
             */
            String applicationSql =
                    "INSERT INTO leave_applications "
                    + "(user_id, leave_type_id, start_date, "
                    + "end_date, reason) "
                    + "VALUES (?, ?, ?, ?, ?)";

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
             * Deduct leave balance.
             */
            String updateBalanceSql =
                    "UPDATE leave_balance "
                    + "SET remaining_days = remaining_days - ? "
                    + "WHERE user_id = ? "
                    + "AND leave_type_id = ?";

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 updateBalanceSql)) {

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

            /*
             * Create submission notification.
             */
            String notificationMessage =
                    "Your leave application has been "
                    + "submitted successfully.";

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
     * Get all applications submitted by one faculty member.
     */
    public List<LeaveApplication> getApplicationsByUserId(
            int userId) {

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

        try (Connection connection =
                     DBConnection.getConnection();
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
     * The cancelled days are restored to the balance.
     */
    public boolean cancelLeave(
            int leaveId,
            int userId) {

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Get the pending application details and lock the row.
             */
            String selectSql =
                    "SELECT leave_type_id, start_date, end_date "
                    + "FROM leave_applications "
                    + "WHERE leave_id = ? "
                    + "AND user_id = ? "
                    + "AND status = 'PENDING' "
                    + "FOR UPDATE";

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
             * Calculate number of days.
             */
            long difference =
                    endDate.getTime() - startDate.getTime();

            int numberOfDays =
                    (int) (difference / (1000 * 60 * 60 * 24)) + 1;

            /*
             * Mark application as cancelled.
             */
            String cancelSql =
                    "UPDATE leave_applications "
                    + "SET status = 'CANCELLED' "
                    + "WHERE leave_id = ? "
                    + "AND user_id = ? "
                    + "AND status = 'PENDING'";

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
             * Restore cancelled leave days.
             */
            String restoreBalanceSql =
                    "UPDATE leave_balance "
                    + "SET remaining_days = remaining_days + ? "
                    + "WHERE user_id = ? "
                    + "AND leave_type_id = ?";

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

            /*
             * Create cancellation notification.
             */
            String notificationMessage =
                    "Your leave application has been "
                    + "cancelled successfully.";

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
     * Get leave applications submitted by faculty
     * belonging to a specific department.
     *
     * This is used by the HOD module.
     *
     * The query also retrieves:
     * - Faculty name
     * - Faculty designation
     * - Department name
     */
    public List<LeaveApplication> getApplicationsByDepartmentId(
            int departmentId) {

        List<LeaveApplication> applications =
                new ArrayList<>();

        String sql =
                "SELECT "
                + "la.leave_id, "
                + "la.user_id, "
                + "u.name AS faculty_name, "
                + "u.designation, "
                + "d.department_name, "
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
                + "JOIN users u "
                + "ON la.user_id = u.user_id "
                + "JOIN departments d "
                + "ON u.department_id = d.department_id "
                + "WHERE u.department_id = ? "
                + "AND u.role = 'FACULTY' "
                + "ORDER BY la.applied_date DESC";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, departmentId);

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

                    application.setFacultyName(
                            resultSet.getString("faculty_name")
                    );

                    application.setDesignation(
                            resultSet.getString("designation")
                    );

                    application.setDepartmentName(
                            resultSet.getString("department_name")
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
     * Get one leave application by its ID.
     *
     * This is used by the HOD details page.
     */
    public LeaveApplication getApplicationById(
        int leaveId) {

    LeaveApplication application = null;

    String sql =
            "SELECT "
            + "la.leave_id, "
            + "la.user_id, "
            + "u.name AS faculty_name, "
            + "u.designation, "
            + "d.department_name, "
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
            + "JOIN users u "
            + "ON la.user_id = u.user_id "
            + "JOIN departments d "
            + "ON u.department_id = d.department_id "
            + "WHERE la.leave_id = ?";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, leaveId);

        try (ResultSet resultSet =
                     statement.executeQuery()) {

            if (resultSet.next()) {

                application =
                        new LeaveApplication();

                application.setLeaveId(
                        resultSet.getInt("leave_id")
                );

                application.setUserId(
                        resultSet.getInt("user_id")
                );

                application.setFacultyName(
                        resultSet.getString("faculty_name")
                );

                application.setDesignation(
                        resultSet.getString("designation")
                );

                application.setDepartmentName(
                        resultSet.getString("department_name")
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
            }
        }

    } catch (SQLException e) {

        e.printStackTrace();
    }

    return application;
}

    /*
     * Check whether a leave application belongs to a
     * faculty member in the specified department.
     *
     * This prevents an HOD from accessing another
     * department's application by changing leaveId.
     */
    public boolean isApplicationInDepartment(
            int leaveId,
            int departmentId) {

        String sql =
                "SELECT la.leave_id "
                + "FROM leave_applications la "
                + "JOIN users u "
                + "ON la.user_id = u.user_id "
                + "WHERE la.leave_id = ? "
                + "AND u.department_id = ? "
                + "AND u.role = 'FACULTY'";

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, leaveId);
            statement.setInt(2, departmentId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next();
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }

    /*
     * Approve a leave application.
     *
     * The update succeeds only when:
     * 1. The application belongs to the HOD's department.
     * 2. The applicant is a FACULTY member.
     * 3. The application is currently PENDING.
     *
     * Approval does not change leave balance because
     * the balance was already deducted when the faculty
     * submitted the application.
     */
    public boolean approveLeave(
            int leaveId,
            int departmentId,
            String hodRemarks) {

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Find the pending application and faculty member.
             */
            String selectSql =
                    "SELECT la.user_id "
                    + "FROM leave_applications la "
                    + "JOIN users u "
                    + "ON la.user_id = u.user_id "
                    + "WHERE la.leave_id = ? "
                    + "AND u.department_id = ? "
                    + "AND u.role = 'FACULTY' "
                    + "AND la.status = 'PENDING' "
                    + "FOR UPDATE";

            int userId;

            try (PreparedStatement statement =
                         connection.prepareStatement(selectSql)) {

                statement.setInt(1, leaveId);
                statement.setInt(2, departmentId);

                try (ResultSet resultSet =
                             statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    userId =
                            resultSet.getInt("user_id");
                }
            }

            /*
             * Approve the application.
             */
            String approveSql =
                    "UPDATE leave_applications "
                    + "SET status = 'APPROVED', "
                    + "hod_remarks = ? "
                    + "WHERE leave_id = ? "
                    + "AND status = 'PENDING'";

            try (PreparedStatement statement =
                         connection.prepareStatement(approveSql)) {

                statement.setString(1, hodRemarks);
                statement.setInt(2, leaveId);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            /*
             * Create approval notification.
             */
            String notificationMessage =
                    "Your leave application #" + leaveId
                    + " has been APPROVED by the HOD.";

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
        }

        return false;
    }

    /*
     * Reject a leave application.
     *
     * The update succeeds only when:
     * 1. The application belongs to the HOD's department.
     * 2. The applicant is a FACULTY member.
     * 3. The application is currently PENDING.
     *
     * Rejection restores the leave balance because
     * the balance was deducted when the application
     * was submitted.
     */
    public boolean rejectLeave(
            int leaveId,
            int departmentId,
            String hodRemarks) {

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * Get the pending application details and lock the row.
             */
            String selectSql =
                    "SELECT "
                    + "la.user_id, "
                    + "la.leave_type_id, "
                    + "la.start_date, "
                    + "la.end_date "
                    + "FROM leave_applications la "
                    + "JOIN users u "
                    + "ON la.user_id = u.user_id "
                    + "WHERE la.leave_id = ? "
                    + "AND u.department_id = ? "
                    + "AND u.role = 'FACULTY' "
                    + "AND la.status = 'PENDING' "
                    + "FOR UPDATE";

            int userId;
            int leaveTypeId;
            Date startDate;
            Date endDate;

            try (PreparedStatement statement =
                         connection.prepareStatement(selectSql)) {

                statement.setInt(1, leaveId);
                statement.setInt(2, departmentId);

                try (ResultSet resultSet =
                             statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    userId =
                            resultSet.getInt("user_id");

                    leaveTypeId =
                            resultSet.getInt("leave_type_id");

                    startDate =
                            resultSet.getDate("start_date");

                    endDate =
                            resultSet.getDate("end_date");
                }
            }

            /*
             * Calculate number of leave days.
             */
            long difference =
                    endDate.getTime() - startDate.getTime();

            int numberOfDays =
                    (int) (difference / (1000 * 60 * 60 * 24)) + 1;

            if (numberOfDays <= 0) {
                connection.rollback();
                return false;
            }

            /*
             * Change application status to REJECTED.
             */
            String rejectSql =
                    "UPDATE leave_applications "
                    + "SET status = 'REJECTED', "
                    + "hod_remarks = ? "
                    + "WHERE leave_id = ? "
                    + "AND status = 'PENDING'";

            try (PreparedStatement statement =
                         connection.prepareStatement(rejectSql)) {

                statement.setString(1, hodRemarks);
                statement.setInt(2, leaveId);

                int rowsAffected =
                        statement.executeUpdate();

                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
            }

            /*
             * Restore rejected leave days.
             */
            String restoreBalanceSql =
                    "UPDATE leave_balance "
                    + "SET remaining_days = remaining_days + ? "
                    + "WHERE user_id = ? "
                    + "AND leave_type_id = ?";

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

            /*
             * Create rejection notification.
             */
            String notificationMessage =
                    "Your leave application #" + leaveId
                    + " has been REJECTED by the HOD.";

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
        }

        return false;
    }
} 