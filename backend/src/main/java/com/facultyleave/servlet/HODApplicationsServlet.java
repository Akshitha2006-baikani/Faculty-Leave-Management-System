package com.facultyleave.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveApplicationDAO;
import com.facultyleave.model.LeaveApplication;

@WebServlet("/hod-applications")
public class HODApplicationsServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // No logged-in user
        if (session == null) {
            response.sendRedirect("login.html?error=unauthorized");
            return;
        }

        // Only HOD can access this servlet
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

        // Get the HOD's department from the session
        Object departmentObject =
                session.getAttribute("departmentId");

        if (departmentObject == null) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "HOD department is not assigned."
            );

            return;
        }

        int departmentId =
                ((Number) departmentObject).intValue();

        // Get only applications from this department
        List<LeaveApplication> applications =
                leaveApplicationDAO
                        .getApplicationsByDepartmentId(
                                departmentId
                        );

        request.setAttribute(
                "applications",
                applications
        );

        request.getRequestDispatcher(
                "hod-applications.jsp"
        ).forward(request, response);
    }
}