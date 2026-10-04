
package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.LeaveApplicationDAO;

@WebServlet("/cancel-leave")
public class CancelLeaveServlet extends HttpServlet {

    private final LeaveApplicationDAO leaveApplicationDAO =
            new LeaveApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        // Check whether faculty is logged in
        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect("login.html?error=session");
            return;
        }

        // Get logged-in faculty user ID
        int userId =
                (Integer) session.getAttribute("userId");

        // Get leave ID from the form
        String leaveIdParameter =
                request.getParameter("leaveId");

        if (leaveIdParameter == null ||
                leaveIdParameter.trim().isEmpty()) {

            response.sendRedirect("my-applications?error=invalid");
            return;
        }

        int leaveId;

        try {
            leaveId = Integer.parseInt(leaveIdParameter);
        } catch (NumberFormatException e) {

            response.sendRedirect("my-applications?error=invalid");
            return;
        }

        // Cancel only if the leave belongs to this faculty
        // and its status is still PENDING
        boolean cancelled =
                leaveApplicationDAO.cancelLeave(
                        leaveId,
                        userId
                );

        if (cancelled) {

            response.sendRedirect(
                    "my-applications?success=cancelled"
            );

        } else {

            response.sendRedirect(
                    "my-applications?error=not-cancellable"
            );
        }
    }
}
