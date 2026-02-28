package com.company.payroll.service;

import com.company.payroll.dto.HrPayrollView;
import com.company.payroll.dto.PayrollResponse;
import com.company.payroll.entity.Payroll;

import java.time.YearMonth;
import java.util.List;

public interface PayrollService {
    List<HrPayrollView> getHrPayrollView(YearMonth payMonth);

    byte[] downloadPayroll(Long employeeId, YearMonth payMonth);

    PayrollResponse getEmployeePayroll(Long employeeId, YearMonth payMonth);

    PayrollResponse generatePayroll(Long employeeId, YearMonth payMonth);
}
