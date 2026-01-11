package com.company.payroll.service;

import com.company.payroll.entity.Payroll;

import java.time.YearMonth;

public interface PayrollService {
    Payroll processMonthlyPayroll(Long employeeId, YearMonth payMonth);

}
