package com.company.payroll.controller;

import com.company.payroll.dto.HrPayrollView;
import com.company.payroll.dto.PayrollResponse;
import com.company.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PayrollController {

    private final PayrollService payrollService;

    /**
     * HR screen:
     * Shows all employees for selected month.
     */
    @GetMapping("/hr/payroll-view")
    public List<HrPayrollView> getHrPayrollView(@RequestParam int year,
                                                @RequestParam int month) {

        YearMonth ym = YearMonth.of(year, month);
        return payrollService.getHrPayrollView(ym);
    }

    /**
     * Employee view payroll.
     * If payroll not generated → error.
     */
    @GetMapping("/employee/payroll")
    public PayrollResponse getEmployeePayroll(@RequestParam Long employeeId,
                                              @RequestParam int year,
                                              @RequestParam int month) {

        YearMonth ym = YearMonth.of(year, month);
        return payrollService.getEmployeePayroll(employeeId, ym);
    }

    /**
     * HR generate payroll for specific employee.
     */
    @PostMapping("/hr/generate")
    public PayrollResponse generatePayroll(@RequestParam Long employeeId,
                                           @RequestParam int year,
                                           @RequestParam int month) {
        YearMonth ym = YearMonth.of(year, month);
        return payrollService.generatePayroll(employeeId, ym);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPayroll(
            @RequestParam Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {

        byte[] pdf = payrollService.downloadPayroll(
                employeeId,
                YearMonth.of(year, month)
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=payslip.pdf")
                .body(pdf);
    }
}