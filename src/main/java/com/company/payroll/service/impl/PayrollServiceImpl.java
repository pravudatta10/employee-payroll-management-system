package com.company.payroll.service.impl;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.SalaryStructure;
import com.company.payroll.entity.enums.LeaveStatus;
import com.company.payroll.entity.enums.PayrollStatus;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.repository.SalaryStructureRepository;
import com.company.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private static final int STANDARD_WORKING_DAYS = 22;
    private static final int FREE_LEAVE_DAYS_PER_MONTH = 2;

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public Payroll processMonthlyPayroll(Long employeeId, YearMonth payMonth) {

        // STEP 1: Prevent duplicate payroll
        payrollRepository.findByEmployeeIdAndPayMonth(employeeId, payMonth)
                .ifPresent(p -> {
                    throw new IllegalStateException(
                            "Payroll already processed for " + payMonth);
                });

        // STEP 2: Fetch employee and salary structure
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        SalaryStructure salary = salaryStructureRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Salary structure not found"));

        // STEP 3: Calculate gross salary
        BigDecimal grossSalary = salary.getBasicSalary()
                .add(salary.getHra())
                .add(salary.getAllowances());

        // STEP 4: Fetch approved leaves for the payroll month
        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findApprovedLeavesForEmployee(
                        employeeId,
                        payMonth.getMonthValue(),
                        payMonth.getYear(),
                        LeaveStatus.APPROVED
                );

        int totalApprovedLeaveDays = approvedLeaves.stream()
                .mapToInt(LeaveRequest::getTotalDays)
                .sum();

        // STEP 5: Calculate unpaid leave days
        int unpaidLeaveDays = Math.max(totalApprovedLeaveDays - FREE_LEAVE_DAYS_PER_MONTH, 0);

        // STEP 6: Per-day salary & leave deduction
        BigDecimal perDaySalary = grossSalary
                .divide(BigDecimal.valueOf(STANDARD_WORKING_DAYS), 2, RoundingMode.HALF_UP);

        BigDecimal leaveDeduction = perDaySalary.multiply(BigDecimal.valueOf(unpaidLeaveDays));

        // STEP 7: Statutory deductions
        BigDecimal pfAmount = grossSalary
                .multiply(salary.getPfPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = grossSalary
                .multiply(salary.getTaxPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // STEP 8: Total Deductions
        BigDecimal totalDeductions = pfAmount.add(taxAmount).add(leaveDeduction);

        // STEP 9: Net Salary
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);

        // STEP 10: Build and save payroll record
        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payMonth(payMonth)
                .grossSalary(grossSalary)
                .pfAmount(pfAmount)
                .taxAmount(taxAmount)
                .leaveDeduction(leaveDeduction)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary)
                .processedDate(LocalDate.now())
                .status(PayrollStatus.GENERATED) // mark payroll as processed
                .build();

        return payrollRepository.save(payroll);
    }
}
