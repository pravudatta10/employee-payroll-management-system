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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollCalculatorService {
    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private static final int FREE_LEAVE_DAYS_PER_MONTH = 2;

    Payroll calculatePayroll(Long employeeId, YearMonth payMonth) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        SalaryStructure salary = salaryStructureRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Salary structure not found"));

        LocalDate start = payMonth.atDay(1);
        LocalDate end = payMonth.atEndOfMonth();

        BigDecimal grossSalary =
                salary.getBasicSalary()
                        .add(salary.getHra())
                        .add(salary.getAllowances());

        int workingDays = calculateWorkingDays(payMonth);

        List<LeaveRequest> leaves =
                leaveRequestRepository.findLeavesForPayrollMonth(
                        employeeId,
                        LeaveStatus.APPROVED,
                        start,
                        end
                );

        int totalLeaveDays = calculateLeaveDaysInMonth(leaves, payMonth);

        int unpaidLeaveDays =
                Math.max(totalLeaveDays - FREE_LEAVE_DAYS_PER_MONTH, 0);

        BigDecimal perDaySalary =
                grossSalary.divide(BigDecimal.valueOf(workingDays), 4, RoundingMode.HALF_UP);

        BigDecimal leaveDeduction =
                perDaySalary.multiply(BigDecimal.valueOf(unpaidLeaveDays))
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal pf = calculatePercentage(grossSalary, salary.getPfPercentage());
        BigDecimal tax = calculatePercentage(grossSalary, salary.getTaxPercentage());

        BigDecimal totalDeductions = pf.add(tax).add(leaveDeduction);
        BigDecimal netSalary = grossSalary.subtract(totalDeductions)
                .setScale(2, RoundingMode.HALF_UP);

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payMonth(payMonth)
                .grossSalary(grossSalary)
                .pfAmount(pf)
                .taxAmount(tax)
                .leaveDeduction(leaveDeduction)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary)
                .workingDays(workingDays)
                .paidDays(workingDays - unpaidLeaveDays)
                .lopDays(unpaidLeaveDays)
                .status(PayrollStatus.GENERATED)
                .processedDate(LocalDate.now())
                .build();

        return payrollRepository.save(payroll);
    }

    private int calculateLeaveDaysInMonth(List<LeaveRequest> leaves, YearMonth payMonth) {

        if (leaves == null || leaves.isEmpty()) {
            return 0;
        }

        LocalDate monthStart = payMonth.atDay(1);
        LocalDate monthEnd = payMonth.atEndOfMonth();

        return leaves.stream()
                .mapToInt(leave -> {

                    LocalDate effectiveStart =
                            leave.getFromDate().isBefore(monthStart)
                                    ? monthStart
                                    : leave.getFromDate();

                    LocalDate effectiveEnd =
                            leave.getToDate().isAfter(monthEnd)
                                    ? monthEnd
                                    : leave.getToDate();

                    if (effectiveStart.isAfter(effectiveEnd)) {
                        return 0;
                    }

                    return (int) (ChronoUnit.DAYS.between(
                            effectiveStart,
                            effectiveEnd
                    ) + 1);
                })
                .sum();
    }

    private BigDecimal calculatePercentage(BigDecimal base,
                                           BigDecimal percentage) {

        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return base.multiply(percentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private int calculateWorkingDays(YearMonth payMonth) {

        LocalDate start = payMonth.atDay(1);
        LocalDate end = payMonth.atEndOfMonth();

        int workingDays = 0;

        while (!start.isAfter(end)) {

            if (start.getDayOfWeek().getValue() < 6) { // 1–5 = Mon–Fri
                workingDays++;
            }

            start = start.plusDays(1);
        }

        return workingDays;
    }
}
