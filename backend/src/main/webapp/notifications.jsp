<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facultyleave.model.Notification" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>
        Notifications - Faculty Leave Management System
    </title>

    <link rel="stylesheet"
          href="css/style.css">

</head>

<body>

    <header class="dashboard-header">

        <div>
            <h1>Faculty Leave Management System</h1>
        </div>

        <div>
            <button
                type="button"
                class="logout-button"
                onclick="window.location.href='login.html'">
                Logout
            </button>
        </div>

    </header>


    <main class="dashboard-container">

        <h2>Notifications</h2>

        <p>
            View updates and notifications related to your leave applications.
        </p>


        <div class="notifications-container">

            <%
                List<Notification> notifications =
                    (List<Notification>)
                    request.getAttribute("notifications");

                if (notifications != null &&
                    !notifications.isEmpty()) {

                    for (Notification notification :
                         notifications) {
            %>

                <div class="notification-card
                    <%= notification.isRead()
                        ? "read"
                        : "unread" %>">

                    <div class="notification-message">

                        <%= notification.getMessage() %>

                    </div>

                    <div class="notification-date">

    <%= notification.getCreatedAt() %>

</div>

<%
    if (!notification.isRead()) {
%>

    <form method="post"
          action="mark-notification-read"
          class="notification-action">

        <input
            type="hidden"
            name="notificationId"
            value="<%= notification.getNotificationId() %>">

        <button
            type="submit"
            class="mark-read-button">
            Mark as Read
        </button>

    </form>

<%
    }
%>

                </div>

            <%
                    }

                } else {
            %>

                <div class="no-data">

                    No notifications available.

                </div>

            <%
                }
            %>

        </div>


        <div class="form-actions">

            <button
                type="button"
                class="cancel-button"
                onclick="window.location.href='faculty-dashboard.html'">
                Back to Dashboard
            </button>

        </div>

    </main>

</body>

</html>