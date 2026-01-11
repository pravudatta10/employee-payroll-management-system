package com.company.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OnboardingResponseDto {
    private String empCode;
    private String fullName;
    private String deptName;
    private String designation;
    private LocalDate joiningDate;
}
