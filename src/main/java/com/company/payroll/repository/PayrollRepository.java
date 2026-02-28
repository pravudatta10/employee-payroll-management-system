package com.company.payroll.repository;

import com.company.payroll.dto.PayrollResponse;
import com.company.payroll.entity.Employee;
import com.company.payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeAndPayMonth(Employee employee, YearMonth payMonth);

    List<Payroll> findByEmployee(Employee employee);

    boolean existsByEmployeeAndPayMonth(Employee employee, YearMonth payMonth);

    Optional<Payroll> findByEmployeeIdAndPayMonth(Long employeeId, YearMonth payMonth);

    List<Payroll> findByPayMonth(YearMonth payMonth);
}
