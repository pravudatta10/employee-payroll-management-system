package com.company.payroll.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PayrollRequest(

        @NotNull
        Long employeeId,

        @Min(2000)
        @Max(2100)
        int year,

        @Min(1)
        @Max(12)
        int month
) {
}