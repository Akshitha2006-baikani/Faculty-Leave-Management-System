
USE faculty_leave_management;

-- Insert departments
INSERT IGNORE INTO departments (department_id, department_name)
VALUES
(1, 'Information Technology'),
(2, 'Computer Science and Engineering'),
(3, 'Electronics and Communication Engineering'),
(4, 'Electrical and Electronics Engineering');

-- Insert leave types
INSERT IGNORE INTO leave_types (leave_type_id, leave_name, total_days)
VALUES
(1, 'Casual Leave', 12),
(2, 'Sick Leave', 10),
(3, 'Earned Leave', 15),
(4, 'On Duty', 20);

-- Create demonstration HOD accounts
INSERT IGNORE INTO users
(name, designation, email, password, role, department_id)
VALUES
('IT HOD', 'Professor & HOD', 'ithod@vce.ac.in', 'Hod@123', 'HOD', 1),
('CSE HOD', 'Professor & HOD', 'csehod@vce.ac.in', 'Cse@123', 'HOD', 2),
('ECE HOD', 'Professor & HOD', 'ecehod@vce.ac.in', 'Ece@123', 'HOD', 3),
('EEE HOD', 'Professor & HOD', 'eeehod@vce.ac.in', 'Eee@123', 'HOD', 4);

-- Create a demonstration faculty account
INSERT IGNORE INTO users
(name, designation, email, password, role, department_id)
VALUES
('Test Faculty', 'Assistant Professor',
 'testfaculty@gmail.com', 'Test@123', 'FACULTY', 1);

-- Initialize leave balances for the demo faculty account
INSERT INTO leave_balance (user_id, leave_type_id, remaining_days)
SELECT u.user_id, lt.leave_type_id, lt.total_days
FROM users u
CROSS JOIN leave_types lt
WHERE u.email = 'testfaculty@gmail.com'
AND u.role = 'FACULTY'
AND NOT EXISTS (
    SELECT 1
    FROM leave_balance lb
    WHERE lb.user_id = u.user_id
      AND lb.leave_type_id = lt.leave_type_id
);
