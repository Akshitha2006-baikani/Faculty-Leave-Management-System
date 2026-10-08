<%@ page import="java.util.List" %>
<%@ page import="com.facultyleave.model.LeaveApplication" %>

<%
    List<LeaveApplication> applications =
            (List<LeaveApplication>) request.getAttribute("applications");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>HOD Applications - Faculty Leave Management</title>

    <link rel="stylesheet" href="css/style.css">

</head>

<body>

<div class="dashboard-container">

    <header class="dashboard-header">

        <div>
            <h1>Faculty Leave Management System</h1>
            <p>Department Leave Applications</p>
        </div>

        <a href="hod-dashboard.html" class="logout-button">
            Back to Dashboard
        </a>

    </header>


    <main class="dashboard-content">

        <section class="welcome-card">

            <h2>Department Leave Applications</h2>

            <p>
                View leave applications submitted by faculty
                members in your department.
            </p>

        </section>


        <section class="dashboard-card">

            <% if (applications == null || applications.isEmpty()) { %>

                <h3>No Leave Applications Found</h3>

                <p>
                    There are no leave applications available
                    for your department.
                </p>

            <% } else { %>

                <div style="overflow-x:auto;">

                    <table
                        border="1"
                        cellpadding="10"
                        cellspacing="0"
                        width="100%">

                        <thead>

                            <tr>
                                <th>Leave ID</th>
                                <th>Faculty ID</th>
                                <th>Faculty Name</th>
                                <th>Designation</th>
                                <th>Department</th>
                                <th>Leave Type</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Reason</th>
                                <th>Status</th>
                                <th>HOD Remarks</th>
                                <th>Applied Date</th>
                                <th>Action</th>
                            </tr>

                        </thead>

                        <tbody>

                        <% for (LeaveApplication leaveApplication : applications) { %>

                            <tr>

                                <td>
                                    <%= leaveApplication.getLeaveId() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getUserId() %>
                                </td>
                                

                                <td>
                                    <%= leaveApplication.getFacultyName() == null
                                            ? "-"
                                            : leaveApplication.getFacultyName() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getDesignation() == null
                                            ? "-"
                                            : leaveApplication.getDesignation() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getDepartmentName() == null
                                            ? "-"
                                            : leaveApplication.getDepartmentName() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getLeaveType() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getStartDate() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getEndDate() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getReason() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getStatus() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getHodRemarks() == null
                                            ? "-"
                                            : leaveApplication.getHodRemarks() %>
                                </td>

                                <td>
                                    <%= leaveApplication.getAppliedDate() %>
                                </td>

                                <td>
    <a href="hod-application-details?leaveId=<%= leaveApplication.getLeaveId() %>">
        <button type="button">
            View Details
        </button>
    </a>
</td>

                            </tr>

                        <% } %>

                        </tbody>

                    </table>

                </div>

            <% } %>

        </section>

    </main>

</div>

</body>

</html>