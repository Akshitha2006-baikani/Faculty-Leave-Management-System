package com.facultyleave.model;

public class User {

    private int userId;
    private String name;
    private String designation;
    private String email;
    private String password;
    private String role;
    private Integer departmentId;
    private String departmentName;

    public User() {
    }

    public User(int userId, String name, String designation, String email,
                String password, String role, Integer departmentId) {
        this.userId = userId;
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
    }

    public User(String name, String designation, String email,
                String password, String role, Integer departmentId) {
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
    }

    // Backward-compatible constructor without designation
public User(int userId, String name, String email,
            String password, String role, Integer departmentId) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.departmentId = departmentId;
}

// Backward-compatible constructor without designation
public User(String name, String email,
            String password, String role, Integer departmentId) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.departmentId = departmentId;
}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDesignation() {
    return designation;
}

    public void setDesignation(String designation) {
    this.designation = designation;
   }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
 
   }
   public String getDepartmentName() {
    return departmentName;
}

public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
}
}