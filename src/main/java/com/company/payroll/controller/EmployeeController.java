package com.company.payroll.controller;

import com.company.payroll.dto.OnboardingRequestDto;
import com.company.payroll.dto.OnboardingResponseDto;
import com.company.payroll.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Onboard a new employee
     * POST /api/v1/employees
     */
    @PostMapping
    public ResponseEntity<OnboardingResponseDto> onBoardNewEmployee(@RequestBody OnboardingRequestDto onboardingRequestDto) {

        OnboardingResponseDto response =
                employeeService.onBoardNewEmployee(onboardingRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
