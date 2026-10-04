package com.facultyleave.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.NotificationDAO;

@WebServlet("/mark-notification-read")
public class MarkNotificationReadServlet extends HttpServlet {

    private final NotificationDAO notificationDAO =
            new NotificationDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    "login.html?error=session"
            );

            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        String notificationIdParameter =
                request.getParameter("notificationId");

        if (notificationIdParameter == null ||
                notificationIdParameter.trim().isEmpty()) {

            response.sendRedirect(
                    "notifications?error=invalid"
            );

            return;
        }

        int notificationId;

        try {

            notificationId =
                    Integer.parseInt(
                            notificationIdParameter
                    );

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    "notifications?error=invalid"
            );

            return;
        }

        boolean markedAsRead =
                notificationDAO.markAsRead(
                        notificationId,
                        userId
                );

        if (markedAsRead) {

            response.sendRedirect(
                    "notifications?success=read"
            );

        } else {

            response.sendRedirect(
                    "notifications?error=failed"
            );
        }
    }
}