
package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("""
            SELECT e FROM Employee e
            WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%')))
            AND (:department IS NULL OR LOWER(e.department) = LOWER(:department))
            """)
    Page<Employee> searchEmployees(
            @Param("name") String name,
            @Param("email") String email,
            @Param("department") String department,
            Pageable pageable
    );
}
