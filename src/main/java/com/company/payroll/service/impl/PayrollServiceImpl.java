package com.company.payroll.service.impl;

import com.company.payroll.dto.HrPayrollView;
import com.company.payroll.dto.PayrollResponse;
import com.company.payroll.entity.Employee;
import com.company.payroll.entity.Payroll;
import com.company.payroll.exception.PayrollNotFoundException;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.PayrollRepository;
import com.company.payroll.service.PaySlipService;
import com.company.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final PaySlipService paySlipService;
    private final PayrollCalculatorService payrollCalculatorService;

    /**
     * HR Payroll View
     * <p>
     * Returns all employees.
     * If payroll not generated for month → status NOT_GENERATED.
     */
    @Override
    public List<HrPayrollView> getHrPayrollView(YearMonth payMonth) {

        List<Employee> employees = employeeRepository.findAll();
        List<Payroll> payrolls = payrollRepository.findByPayMonth(payMonth);

        Map<Long, Payroll> payrollMap = payrolls.stream()
                .collect(Collectors.toMap(
                        p -> p.getEmployee().getId(),
                        p -> p
                ));

        return employees.stream()
                .map(emp -> {

                    Payroll payroll = payrollMap.get(emp.getId());

                    if (payroll == null) {
                        return new HrPayrollView(
                                emp.getId(),
                                emp.getFullName(),
                                emp.getDesignation(),
                                false,
                                "NOT_GENERATED",
                                null,
                                null
                        );
                    }

                    return new HrPayrollView(
                            emp.getId(),
                            emp.getFullName(),
                            emp.getDesignation(),
                            true,
                            payroll.getStatus().name(),
                            payroll.getGrossSalary(),
                            payroll.getNetSalary()
                    );
                })
                .toList();
    }

    /**
     * generate PDF.
     */

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadPayroll(Long employeeId, YearMonth yearMonth) {
        Payroll payroll = payrollRepository
                .findByEmployeeIdAndPayMonth(employeeId, yearMonth)
                .orElseThrow(() ->
                        new PayrollNotFoundException(
                                "Payroll not yet generated. Please contact HR."
                        ));

        return paySlipService.generatePayslipPdf(payroll);
    }

    /**
     * Generate payroll if not exists.
     */
    @Override
    public PayrollResponse generatePayroll(Long employeeId, YearMonth payMonth) {
        Payroll payroll = payrollRepository
                .findByEmployeeIdAndPayMonth(employeeId, payMonth)
                .orElseGet(() -> {
                    //First time → create & save
                    return payrollCalculatorService.calculatePayroll(employeeId, payMonth);
                });
        return PayrollResponse.from(payroll);
    }

    /**
     * Employee view.
     * If payroll not generated → throw error.
     */
    @Override
    public PayrollResponse getEmployeePayroll(Long employeeId, YearMonth payMonth) {

        Payroll payroll = payrollRepository
                .findByEmployeeIdAndPayMonth(employeeId, payMonth)
                .orElseThrow(() ->
                        new IllegalStateException("Payroll not generated yet"));

        return PayrollResponse.from(payroll);
    }
}