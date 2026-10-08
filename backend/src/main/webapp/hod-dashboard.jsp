<%
    String userName =
            (String) session.getAttribute("userName");

    String userDesignation =
            (String) session.getAttribute("userDesignation");

    String departmentName =
            (String) session.getAttribute("departmentName");

    if (userName == null) {
        userName = "HOD";
    }

    if (userDesignation == null
            || userDesignation.trim().isEmpty()) {
        userDesignation = "Head of Department";
    }

    if (departmentName == null
            || departmentName.trim().isEmpty()) {
        departmentName = "Department";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>HOD Dashboard - Faculty Leave Management</title>

    <link rel="stylesheet"
          href="css/style.css">

</head>

<body>

<div class="dashboard-container">

    <header class="dashboard-header">

        <div>

            <h1>Faculty Leave Management System</h1>

            <p>
                Head of Department Portal
            </p>

        </div>

        <a href="logout"
           class="logout-button">
            Logout
        </a>

    </header>


    <main class="dashboard-content">

        <section class="welcome-card">

            <h2>
                Welcome, <%= userName %>!
            </h2>

            <p>
                <strong>
                    <%= userDesignation %>
                </strong>
            </p>

            <p>
                Department:
                <strong>
                    <%= departmentName %>
                </strong>
            </p>

            <p>
                Manage and review leave applications submitted
                by faculty members in your department.
            </p>

        </section>


        <section class="dashboard-grid">

            <div class="dashboard-card">

                <div>

                    <h3>
                        Pending Leave Applications
                    </h3>

                    <p>
                        View and process pending leave applications
                        submitted by faculty members in your department.
                    </p>

                </div>

                <a href="hod-applications">

                    <button type="button">
                        View Applications
                    </button>

                </a>

            </div>


            <div class="dashboard-card">

                <div>

                    <h3>
                        Department Leave Records
                    </h3>

                    <p>
                        View leave application records of faculty
                        members belonging to your department.
                    </p>

                </div>

                <a href="hod-applications">

                    <button type="button">
                        View Records
                    </button>

                </a>

            </div>

        </section>

    </main>

</div>

</body>

</html>