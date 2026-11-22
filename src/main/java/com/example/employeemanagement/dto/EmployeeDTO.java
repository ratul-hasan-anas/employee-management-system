
package com.example.employeemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmployeeDTO {

    private int id;

    // Name cannot be empty
    @NotBlank(message = "Employee name is required")
    private String name;

    // Email must be valid
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    // Department cannot be empty
    @NotBlank(message = "Department is required")
    private String department;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
