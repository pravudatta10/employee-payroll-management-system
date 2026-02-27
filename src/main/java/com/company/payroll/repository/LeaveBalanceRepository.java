package com.company.payroll.repository;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployee(Employee emp);

    Optional<LeaveBalance> findByEmployeeAndLeaveYear(Employee employee, int year);
}
