package com.company.payroll.dto;


import java.math.BigDecimal;

public record HrPayrollView(

        Long employeeId,
        String employeeName,
        String designation,

        boolean payrollGenerated,
        String status,

        BigDecimal grossSalary,
        BigDecimal netSalary

) {
}
