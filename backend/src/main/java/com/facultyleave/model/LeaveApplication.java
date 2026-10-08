package com.facultyleave.model;

public class LeaveApplication {

    private int leaveId;
    private int userId;
    private String facultyName;
    private String designation;
    private String departmentName;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status;
    private String hodRemarks;
    private String appliedDate;

    public LeaveApplication() {
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFacultyName() {
    return facultyName;
}

public void setFacultyName(String facultyName) {
    this.facultyName = facultyName;
}

public String getDesignation() {
    return designation;
}

public void setDesignation(String designation) {
    this.designation = designation;
}

public String getDepartmentName() {
    return departmentName;
}

public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
}

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHodRemarks() {
        return hodRemarks;
    }

    public void setHodRemarks(String hodRemarks) {
        this.hodRemarks = hodRemarks;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }
}