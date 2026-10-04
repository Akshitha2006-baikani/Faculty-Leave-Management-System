package com.facultyleave.servlet;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveApplicationDAO;

@WebServlet("/apply-leave")
public class ApplyLeaveServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the current logged-in user's session
         */
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html?error=session");
            return;
        }

        /*
         * Get faculty user ID from session
         */
        int userId = (Integer) session.getAttribute("userId");

        /*
         * Get form values
         */
        String leaveType = request.getParameter("leaveType");
        String startDateString = request.getParameter("startDate");
        String endDateString = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        /*
         * Basic validation
         */
        if (leaveType == null || leaveType.trim().isEmpty()
                || startDateString == null || startDateString.isEmpty()
                || endDateString == null || endDateString.isEmpty()
                || reason == null || reason.trim().isEmpty()) {

            response.sendRedirect("apply-leave.html?error=empty");
            return;
        }

        /*
         * Convert leave type to database leave_type_id
         *
         * Current database:
         *
         * Casual Leave = 1
         * Sick Leave   = 2
         * Earned Leave = 3
         * On Duty      = 4
         */

        int leaveTypeId;

        switch (leaveType) {

            case "CASUAL":
                leaveTypeId = 1;
                break;

            case "SICK":
                leaveTypeId = 2;
                break;

            case "EARNED":
                leaveTypeId = 3;
                break;

            case "ON_DUTY":
                leaveTypeId = 4;
                break;

            default:
                response.sendRedirect("apply-leave.html?error=leavetype");
                return;
        }

        /*
         * Convert dates
         */
        Date startDate;
        Date endDate;

        try {

            startDate = Date.valueOf(startDateString);
            endDate = Date.valueOf(endDateString);

        } catch (IllegalArgumentException e) {

            response.sendRedirect("apply-leave.html?error=date");
            return;
        }

        /*
         * Check date order
         */
        if (endDate.before(startDate)) {

            response.sendRedirect("apply-leave.html?error=dateorder");
            return;
        }

        /*
         * Insert application into database
         */
        boolean success =
                leaveApplicationDAO.applyLeave(
                        userId,
                        leaveTypeId,
                        startDate,
                        endDate,
                        reason.trim()
                );

        /*
         * Redirect based on result
         */
        if (success) {

    response.sendRedirect(
            "application-success.html"
    );

} else {

    response.sendRedirect(
            "apply-leave.html?error=database"
    );
}
    }


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("apply-leave.html");
    }
}