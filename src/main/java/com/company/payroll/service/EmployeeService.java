package com.company.payroll.service;

import com.company.payroll.dto.OnboardingRequestDto;
import com.company.payroll.dto.OnboardingResponseDto;

public interface EmployeeService {
    OnboardingResponseDto onBoardNewEmployee(OnboardingRequestDto onboardingRequestDto);
}
