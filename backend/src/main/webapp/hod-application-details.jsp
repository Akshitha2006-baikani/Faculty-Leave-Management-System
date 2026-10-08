<%@ page import="com.facultyleave.model.LeaveApplication" %>

<%
    LeaveApplication leaveApplication =
            (LeaveApplication) request.getAttribute("application");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Application Details - Faculty Leave Management</title>

    <link rel="stylesheet" href="css/style.css">
</head>

<body>

<div class="dashboard-container">

    <header class="dashboard-header">

        <div>
            <h1>Faculty Leave Management System</h1>
            <p>Leave Application Details</p>
        </div>

        <a href="hod-applications"
           class="logout-button">
            Back to Applications
        </a>

    </header>

    <main class="dashboard-content">

        <section class="welcome-card">

            <h2>Leave Application Details</h2>

            <p>
                Review the faculty member's leave application
                before taking an action.
            </p>

        </section>

        <section class="dashboard-card">

            <table
                border="1"
                cellpadding="10"
                cellspacing="0"
                width="100%">

                <tr>
                    <th>Leave ID</th>
                    <td>
                        <%= leaveApplication.getLeaveId() %>
                    </td>
                </tr>

                <tr>
                    <th>Faculty ID</th>
                    <td>
                        <%= leaveApplication.getUserId() %>
                    </td>
                </tr>
                <tr>
                    <th>Faculty Name</th>
                    <td>
                        <%= leaveApplication.getFacultyName() == null
                                ? "-"
                                : leaveApplication.getFacultyName() %>
                    </td>
                </tr>

                <tr>
                    <th>Designation</th>
                    <td>
                        <%= leaveApplication.getDesignation() == null
                                ? "-"
                                : leaveApplication.getDesignation() %>
                    </td>
                </tr>

                <tr>
                    <th>Department</th>
                    <td>
                        <%= leaveApplication.getDepartmentName() == null
                                ? "-"
                                : leaveApplication.getDepartmentName() %>
                    </td>
                </tr>

                <tr>
                    <th>Leave Type</th>
                    <td>
                        <%= leaveApplication.getLeaveType() %>
                    </td>
                </tr>

                <tr>
                    <th>Start Date</th>
                    <td>
                        <%= leaveApplication.getStartDate() %>
                    </td>
                </tr>

                <tr>
                    <th>End Date</th>
                    <td>
                        <%= leaveApplication.getEndDate() %>
                    </td>
                </tr>

                <tr>
                    <th>Reason</th>
                    <td>
                        <%= leaveApplication.getReason() %>
                    </td>
                </tr>

                <tr>
                    <th>Status</th>
                    <td>
                        <%= leaveApplication.getStatus() %>
                    </td>
                </tr>

                <tr>
                    <th>HOD Remarks</th>
                    <td>
                        <%= leaveApplication.getHodRemarks() == null
                                ? "-"
                                : leaveApplication.getHodRemarks() %>
                    </td>
                </tr>

                <tr>
                    <th>Applied Date</th>
                    <td>
                        <%= leaveApplication.getAppliedDate() %>
                    </td>
                </tr>

            </table>
            <% if ("PENDING".equals(leaveApplication.getStatus())) { %>

    <div style="margin-top: 20px;">

        <h3>HOD Action</h3>

        <form action="hod-leave-action"
              method="post">

            <input
                type="hidden"
                name="leaveId"
                value="<%= leaveApplication.getLeaveId() %>">

            <label for="hodRemarks">
                HOD Remarks
            </label>

            <br>

            <textarea
                id="hodRemarks"
                name="hodRemarks"
                rows="5"
                cols="60"
                placeholder="Enter remarks for the faculty member..."></textarea>

            <br><br>

            <button
                type="submit"
                name="action"
                value="APPROVE">
                Approve Leave
            </button>

            <button
                type="submit"
                name="action"
                value="REJECT">
                Reject Leave
            </button>

        </form>

    </div>

<% } else { %>

    <p>
        This application has already been
        <strong><%= leaveApplication.getStatus() %></strong>.
        No further action is available.
    </p>

<% } %>

        </section>

    </main>

</div>

</body>
</html>