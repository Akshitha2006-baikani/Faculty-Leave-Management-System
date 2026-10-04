package com.facultyleave.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.facultyleave.dao.NotificationDAO;
import com.facultyleave.model.Notification;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO =
            new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request,
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

        List<Notification> notifications =
                notificationDAO.getNotificationsByUserId(
                        userId
                );

        request.setAttribute(
                "notifications",
                notifications
        );

        request.getRequestDispatcher(
                "notifications.jsp"
        ).forward(request, response);
    }
}