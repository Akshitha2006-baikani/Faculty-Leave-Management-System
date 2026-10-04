<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facultyleave.model.LeaveApplication" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>My Applications | Faculty Leave Management System</title>

    <link rel="stylesheet"
          href="css/style.css">

</head>

<body>

<div class="dashboard-container">

    <!-- HEADER -->

    <header class="dashboard-header">

        <div>

            <h1>My Leave Applications</h1>

            <p>
                Vardhaman College of Engineering
            </p>

        </div>

        <button
            class="logout-button"
            onclick="window.location.href='faculty-dashboard.html'">

            Back to Dashboard

        </button>

    </header>


    <!-- MAIN CONTENT -->

    <main class="dashboard-content">


        <!-- PAGE INTRO -->

        <section class="welcome-card">

            <h2>
                Application History
            </h2>

            <p>
                View the leave applications you have submitted
                and track their current approval status.
            </p>

        </section>


        <!-- APPLICATIONS -->

        <section class="applications-card">

            <%
                List<LeaveApplication> applications =
                    (List<LeaveApplication>)
                    request.getAttribute("applications");

                if (applications == null ||
                    applications.isEmpty()) {
            %>

                <div class="empty-applications">

                    <div class="empty-icon">
                        📋
                    </div>

                    <h3>
                        No Leave Applications
                    </h3>

                    <p>
                        You have not submitted any leave
                        applications yet.
                    </p>

                    <button
                        class="submit-button"
                        onclick="window.location.href='apply-leave.html'">

                        Apply for Leave

                    </button>

                </div>

            <%
                } else {
            %>

                <div class="table-container">

                    <table class="applications-table">

                        <thead>

                            <tr>

                                <th>
                                    Leave ID
                                </th>

                                <th>
                                    Leave Type
                                </th>

                                <th>
                                    Start Date
                                </th>

                                <th>
                                    End Date
                                </th>

                                <th>
                                    Reason
                                </th>

                                <th>
                                    Status
                                </th>

                                <th>
                                    HOD Remarks
                                </th>

                                <th>
                                    Action
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                        <%
                            for (LeaveApplication leave
                                 : applications) {
                        %>

                            <tr>

                                <td>
                                    #<%= leave.getLeaveId() %>
                                </td>

                                <td>
                                    <%= leave.getLeaveType() %>
                                </td>

                                <td>
                                    <%= leave.getStartDate() %>
                                </td>

                                <td>
                                    <%= leave.getEndDate() %>
                                </td>

                                <td>
                                    <%= leave.getReason() %>
                                </td>

                                <td>

                                    
<span class="status-badge
    status-<%= leave.getStatus().toLowerCase() %>">

    <%= leave.getStatus() %>

</span>


                                </td>

                               
<td>
    <%
        String remarks = leave.getHodRemarks();

        if (remarks == null || remarks.trim().isEmpty()) {
    %>
        <span class="no-remarks">—</span>
    <%
        } else {
    %>
        <%= remarks %>
    <%
        }
    %>
</td>

<td>
    <%
        if ("PENDING".equals(leave.getStatus())) {
    %>
        <form action="cancel-leave"
              method="post"
              onsubmit="return confirm('Are you sure you want to cancel this leave application?');">

            <input type="hidden"
                   name="leaveId"
                   value="<%= leave.getLeaveId() %>">

            <button type="submit"
                    class="cancel-leave-button">
                Cancel Leave
            </button>

        </form>
    <%
        } else {
    %>
        <span class="no-action">—</span>
    <%
        }
    %>
</td>


                            </tr>

                        <%
                            }
                        %>

                        </tbody>

                    </table>

                </div>


                <!-- APPLY AGAIN -->

                <div class="form-actions application-actions">

                    <button
                        class="cancel-button"
                        onclick="window.location.href='faculty-dashboard.html'">

                        Back to Dashboard

                    </button>

                    <button
                        class="submit-button"
                        onclick="window.location.href='apply-leave.html'">

                        Apply for New Leave

                    </button>

                </div>

            <%
                }
            %>

        </section>

    </main>

</div>

</body>

</html>