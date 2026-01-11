package com.company.payroll.repository;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    Optional<SalaryStructure> findByEmployee(Employee employee);

    boolean existsByEmployee(Employee employee);

    Optional<SalaryStructure> findByEmployeeId(Long employeeId);
}
