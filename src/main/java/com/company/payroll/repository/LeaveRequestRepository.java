package com.company.payroll.repository;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, LeaveStatus status);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByEmployeeAndFromDateBetween(
            Employee employee,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
                SELECT l FROM LeaveRequest l
                WHERE l.employee.id = :employeeId
                  AND l.status = :status
                  AND FUNCTION('MONTH', l.fromDate) = :month
                  AND FUNCTION('YEAR', l.fromDate) = :year
            """)
    List<LeaveRequest> findApprovedLeavesForEmployee(
            Long employeeId,
            int month,
            int year,
            LeaveStatus status
    );

    @Query("""
       SELECT l FROM LeaveRequest l
       WHERE l.employee.id = :employeeId
       AND l.status = :status
       AND l.fromDate <= :monthEnd
       AND l.toDate >= :monthStart
       """)
    List<LeaveRequest> findLeavesForPayrollMonth(
            Long employeeId,
            LeaveStatus status,
            LocalDate monthStart,
            LocalDate monthEnd
    );
    List<LeaveRequest> findByEmployeeEmpCode(String empCode);
}
