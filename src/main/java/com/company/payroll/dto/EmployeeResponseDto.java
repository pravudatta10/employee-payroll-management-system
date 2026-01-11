package com.company.payroll.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class EmployeeResponseDto {
    private Long id;
    private String empCode;
    private String fullName;
    private String department;
    private String designation;
    private LocalDate joiningDate;

    //salary
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal taxPercentage;
    private BigDecimal pfPercentage;
}
