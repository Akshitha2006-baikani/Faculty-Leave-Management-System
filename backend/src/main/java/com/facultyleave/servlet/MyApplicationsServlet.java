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

@WebServlet("/my-applications")
public class MyApplicationsServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the existing login session
         */
        HttpSession session = request.getSession(false);

        /*
         * If the faculty is not logged in,
         * send them back to the login page.
         */
        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html?error=session");
            return;
        }

        /*
         * Get logged-in faculty user ID
         */
        int userId =
                (Integer) session.getAttribute("userId");

        /*
         * Get applications from database
         */
        List<LeaveApplication> applications =
                leaveApplicationDAO.getApplicationsByUserId(userId);

        /*
         * Store applications in request
         * so the page can use them later.
         */
        request.setAttribute(
                "applications",
                applications
        );

        /*
         * Forward to My Applications page
         */
        request.getRequestDispatcher(
                "my-applications.jsp"
        ).forward(request, response);
    }
}