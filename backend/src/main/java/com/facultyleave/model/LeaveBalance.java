package com.facultyleave.model;

public class LeaveBalance {

    private int leaveTypeId;
    private String leaveTypeName;
    private int remainingDays;

    public LeaveBalance() {
    }

    public LeaveBalance(int leaveTypeId,
                        String leaveTypeName,
                        int remainingDays) {
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
        this.remainingDays = remainingDays;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
}