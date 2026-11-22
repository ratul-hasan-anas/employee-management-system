
package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.response.ApiResponse;
import com.example.employeemanagement.service.EmployeeService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    // Constructor Injection
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ================= CREATE EMPLOYEE =================
    // Only ADMIN can create employee

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(
            @Valid @RequestBody EmployeeDTO dto) {

        logger.info("Create employee request received");

        EmployeeDTO createdEmployee =
                employeeService.createEmployee(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Employee created successfully",
                        createdEmployee
                ));
    }

    // ================= GET ALL EMPLOYEES =================
    // ADMIN and USER can view all employees

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees() {

        logger.info("Get all employees request received");

        List<EmployeeDTO> employees =
                employeeService.getAllEmployees();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employees retrieved successfully",
                        employees
                )
        );
    }

    // ================= GET EMPLOYEE BY ID =================
    // ADMIN and USER can view employee details

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(
            @PathVariable int id) {

        logger.info("Get employee by ID: {}", id);

        EmployeeDTO employee =
                employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee retrieved successfully",
                        employee
                )
        );
    }

    // ================= UPDATE EMPLOYEE =================
    // Only ADMIN can update employee

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable int id,
            @Valid @RequestBody EmployeeDTO dto) {

        logger.info("Update employee ID: {}", id);

        EmployeeDTO updatedEmployee =
                employeeService.updateEmployee(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee updated successfully",
                        updatedEmployee
                )
        );
    }

    // ================= DELETE EMPLOYEE =================
    // Only ADMIN can delete employee

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(
            @PathVariable int id) {

        logger.info("Delete employee ID: {}", id);

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee deleted successfully",
                        "Deleted employee ID: " + id
                )
        );
    }

    // ================= PAGINATION AND SORTING =================
    // ADMIN and USER can view paginated employees

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<EmployeeDTO>>>
    getEmployeesWithPaginationAndSorting(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Pagination request received");

        Page<EmployeeDTO> employees =
                employeeService.getEmployeesWithPaginationAndSorting(
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employees retrieved successfully with pagination",
                        employees
                )
        );
    }

    // ================= SEARCH AND FILTER =================
    // ADMIN and USER can search employees

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeDTO>>> searchEmployees(

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Search employee request received");

        Page<EmployeeDTO> employees =
                employeeService.searchEmployees(
                        name,
                        email,
                        department,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee search completed successfully",
                        employees
                )
        );
    }
}
