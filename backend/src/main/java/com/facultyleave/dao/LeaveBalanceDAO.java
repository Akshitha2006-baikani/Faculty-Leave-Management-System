package com.facultyleave.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.facultyleave.model.LeaveBalance;
import com.facultyleave.util.DBConnection;

public class LeaveBalanceDAO {

    public List<LeaveBalance> getLeaveBalanceByUserId(int userId) {

        List<LeaveBalance> balances = new ArrayList<>();

        String sql =
                "SELECT lb.leave_type_id, " +
                "lt.leave_name, " +
                "lb.remaining_days " +
                "FROM leave_balance lb " +
                "JOIN leave_types lt " +
                "ON lb.leave_type_id = lt.leave_type_id " +
                "WHERE lb.user_id = ? " +
                "ORDER BY lb.leave_type_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    LeaveBalance balance = new LeaveBalance();

                    balance.setLeaveTypeId(
                            resultSet.getInt("leave_type_id")
                    );

                    balance.setLeaveTypeName(
                            resultSet.getString("leave_name")
                    );

                    balance.setRemainingDays(
                            resultSet.getInt("remaining_days")
                    );

                    balances.add(balance);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balances;
    }
}