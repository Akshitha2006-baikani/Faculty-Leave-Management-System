package com.facultyleave.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveBalanceDAO;
import com.facultyleave.model.LeaveBalance;

@WebServlet("/leave-balance")
public class LeaveBalanceServlet extends HttpServlet {

    private final LeaveBalanceDAO leaveBalanceDAO =
            new LeaveBalanceDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html?error=session");
            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        List<LeaveBalance> balances =
                leaveBalanceDAO.getLeaveBalanceByUserId(userId);

        request.setAttribute(
                "balances",
                balances
        );

        request.getRequestDispatcher(
                "leave-balance.jsp"
        ).forward(request, response);
    }
}