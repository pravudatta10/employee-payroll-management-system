package com.company.payroll.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OnboardingRequestDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    //salary entry
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal taxPercentage;
    private BigDecimal pfPercentage;
}
