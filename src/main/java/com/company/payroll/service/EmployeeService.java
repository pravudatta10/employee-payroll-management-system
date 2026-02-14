package com.company.payroll.service;

import com.company.payroll.dto.EmployeeResponseDto;
import com.company.payroll.dto.OnboardingRequestDto;
import com.company.payroll.dto.OnboardingResponseDto;
import com.company.payroll.entity.Employee;

import java.util.List;

public interface EmployeeService {
    OnboardingResponseDto onBoardNewEmployee(OnboardingRequestDto onboardingRequestDto);
    OnboardingResponseDto updateEmployeeRecord(String empCode,OnboardingRequestDto onboardingRequestDto);
    void deleteEmployeeRecord(String empCode);
    List<EmployeeResponseDto> getAllActiveEmployees();
    EmployeeResponseDto getActiveEmployeeByEmpCode(String empCode);
}
