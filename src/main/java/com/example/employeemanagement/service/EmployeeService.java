
package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    // Constructor Injection
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Entity to DTO Conversion
    private EmployeeDTO convertToDTO(Employee employee) {

        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setDepartment(employee.getDepartment());

        return dto;
    }

    // DTO to Entity Conversion
    private Employee convertToEntity(EmployeeDTO dto) {

        Employee employee = new Employee();

        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());

        return employee;
    }

    // CREATE EMPLOYEE
    public EmployeeDTO createEmployee(EmployeeDTO dto) {

        Employee employee = convertToEntity(dto);

        Employee savedEmployee =
                employeeRepository.save(employee);

        logger.info("Employee created with ID: {}",
                savedEmployee.getId());

        return convertToDTO(savedEmployee);
    }

    // GET ALL EMPLOYEES
    public List<EmployeeDTO> getAllEmployees() {

        List<Employee> employees =
                employeeRepository.findAll();

        logger.info("Total employees found: {}",
                employees.size());

        return employees.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // GET EMPLOYEE BY ID
    public EmployeeDTO getEmployeeById(int id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee not found: {}", id);

                    return new ResourceNotFoundException(
                            "Employee not found with ID: " + id
                    );
                });

        return convertToDTO(employee);
    }

    // UPDATE EMPLOYEE
    public EmployeeDTO updateEmployee(
            int id,
            EmployeeDTO dto) {

        Employee existingEmployee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.warn(
                                    "Employee not found for update: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Employee not found with ID: " + id
                            );
                        });

        existingEmployee.setName(dto.getName());
        existingEmployee.setEmail(dto.getEmail());
        existingEmployee.setDepartment(dto.getDepartment());

        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        logger.info("Employee updated: {}", id);

        return convertToDTO(updatedEmployee);
    }

    // DELETE EMPLOYEE
    public void deleteEmployee(int id) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.warn(
                                    "Employee not found for deletion: {}",
                                    id
                            );

                            return new ResourceNotFoundException(
                                    "Employee not found with ID: " + id
                            );
                        });

        employeeRepository.delete(employee);

        logger.info("Employee deleted: {}", id);
    }

    // PAGINATION AND SORTING
    public Page<EmployeeDTO> getEmployeesWithPaginationAndSorting(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Employee> employeePage =
                employeeRepository.findAll(pageable);

        logger.info(
                "Pagination executed. Page: {}, Size: {}",
                page, size
        );

        return employeePage.map(this::convertToDTO);
    }

    // SEARCH AND FILTER + PAGINATION + SORTING
    public Page<EmployeeDTO> searchEmployees(
            String name,
            String email,
            String department,
            int page,
            int size,
            String sortBy,
            String direction) {

        // Empty values converted to null
        name = (name == null || name.isBlank())
                ? null : name.trim();

        email = (email == null || email.isBlank())
                ? null : email.trim();

        department = (department == null || department.isBlank())
                ? null : department.trim();

        // Sorting
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // Pagination
        Pageable pageable =
                PageRequest.of(page, size, sort);

        // Repository query execution
        Page<Employee> employeePage =
                employeeRepository.searchEmployees(
                        name,
                        email,
                        department,
                        pageable
                );

        logger.info(
                "Search executed. Name: {}, Email: {}, Department: {}",
                name, email, department
        );

        // Entity to DTO conversion
        return employeePage.map(this::convertToDTO);
    }
}
