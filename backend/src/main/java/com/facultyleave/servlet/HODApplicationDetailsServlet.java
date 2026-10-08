package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveApplicationDAO;
import com.facultyleave.model.LeaveApplication;

@WebServlet("/hod-application-details")
public class HODApplicationDetailsServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doGet(
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

        if (leaveIdParameter == null
                || leaveIdParameter.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Leave application ID is required."
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

        LeaveApplication application =
                leaveApplicationDAO
                        .getApplicationById(leaveId);

        if (application == null) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Leave application not found."
            );
            return;
        }

        /*
         * Verify that this application belongs
         * to a faculty member in the HOD's department.
         */
        boolean belongsToDepartment =leaveApplicationDAO.isApplicationInDepartment( leaveId,departmentId);

        if (!belongsToDepartment) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access denied. Application does not belong to your department."
            );
            return;
        }

        request.setAttribute(
                "application",
                application
        );

        request.getRequestDispatcher(
                "hod-application-details.jsp"
        ).forward(request, response);
    }
}