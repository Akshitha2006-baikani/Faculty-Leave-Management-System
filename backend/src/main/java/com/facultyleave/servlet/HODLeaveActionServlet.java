package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveApplicationDAO;

@WebServlet("/hod-leave-action")
public class HODLeaveActionServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    "login.html?error=unauthorized"
            );
            return;
        }

        Object roleObject =
                session.getAttribute("userRole");

        if (roleObject == null
                || !"HOD".equals(roleObject.toString())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. HOD access only."
            );
            return;
        }

        Object departmentObject =
                session.getAttribute("departmentId");

        if (departmentObject == null) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "HOD department is not assigned."
            );
            return;
        }

        String leaveIdParameter =
                request.getParameter("leaveId");

        String action =
                request.getParameter("action");

        String hodRemarks =
                request.getParameter("hodRemarks");

        if (leaveIdParameter == null
                || leaveIdParameter.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Leave application ID is required."
            );
            return;
        }

        if (action == null
                || action.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Action is required."
            );
            return;
        }

        int leaveId;

        try {

            leaveId =
                    Integer.parseInt(
                            leaveIdParameter
                    );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid leave application ID."
            );
            return;
        }

        int departmentId =
                ((Number) departmentObject).intValue();

        if (hodRemarks == null) {
            hodRemarks = "";
        }

        hodRemarks = hodRemarks.trim();

        boolean success;

        if ("APPROVE".equalsIgnoreCase(action)) {

            success =
                    leaveApplicationDAO.approveLeave(
                            leaveId,
                            departmentId,
                            hodRemarks
                    );

        } else if ("REJECT".equalsIgnoreCase(action)) {

            success =
                    leaveApplicationDAO.rejectLeave(
                            leaveId,
                            departmentId,
                            hodRemarks
                    );

        } else {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid action."
            );
            return;
        }

        if (success) {

            response.sendRedirect(
                    "hod-applications?message=success"
            );

        } else {

            response.sendRedirect(
                    "hod-applications?message=failed"
            );
        }
    }
}