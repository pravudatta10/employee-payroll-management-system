package com.company.payroll.dto;

import com.company.payroll.entity.Payroll;

import java.math.BigDecimal;

public record PayrollResponse(

        Long payrollId,
        Long employeeId,
        String employeeName,
        String designation,
        String payMonth,

        BigDecimal grossSalary,
        BigDecimal totalDeductions,
        BigDecimal netSalary,

        Integer workingDays,
        Integer paidDays,
        Integer lopDays,

        String status

) {

    public static PayrollResponse from(Payroll payroll) {

        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getEmployee().getFullName(),
                payroll.getEmployee().getDesignation(),
                payroll.getPayMonth().toString(),

                payroll.getGrossSalary(),
                payroll.getTotalDeductions(),
                payroll.getNetSalary(),

                payroll.getWorkingDays(),
                payroll.getPaidDays(),
                payroll.getLopDays(),

                payroll.getStatus().name()
        );
    }
}