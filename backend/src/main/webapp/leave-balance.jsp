<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facultyleave.model.LeaveBalance" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

    <title>Leave Balance - Faculty Leave Management System</title>

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

        <h2>Leave Balance</h2>

        <p>
            View your remaining leave days for each leave type.
        </p>


        <div class="leave-balance-container">

            <%
                List<LeaveBalance> balances =
                    (List<LeaveBalance>)
                    request.getAttribute("balances");

                if (balances != null &&
                    !balances.isEmpty()) {

                    for (LeaveBalance balance : balances) {
            %>

                <div class="leave-balance-card">

                    <h3>
                        <%= balance.getLeaveTypeName() %>
                    </h3>

                    <div class="balance-number">
                        <%= balance.getRemainingDays() %>
                    </div>

                    <p>
                        Days Remaining
                    </p>

                </div>

            <%
                    }

                } else {
            %>

                <div class="no-data">
                    No leave balance information available.
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

            <button
                type="button"
                class="submit-button"
                onclick="window.location.href='apply-leave.html'">
                Apply for Leave
            </button>

        </div>

    </main>

</body>
</html>