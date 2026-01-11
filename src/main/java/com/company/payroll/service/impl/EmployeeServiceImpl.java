package com.company.payroll.service.impl;

import com.company.payroll.dto.OnboardingRequestDto;
import com.company.payroll.dto.OnboardingResponseDto;
import com.company.payroll.entity.Employee;
import com.company.payroll.entity.SalaryStructure;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.SalaryStructureRepository;
import com.company.payroll.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public OnboardingResponseDto onBoardNewEmployee(OnboardingRequestDto request) {

        // 1. Validate duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employee with this email already exists");
        }

        // 2. Generate unique employee code
        String empCode = generateUniqueEmpCode();

        // 3. Build full name safely
        String fullName = buildFullName(
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName()
        );

        // 4. Save employee
        Employee employee = Employee.builder()
                .empCode(empCode)
                .fullName(fullName)
                .email(request.getEmail())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .active(true)
                .build();

        employeeRepository.save(employee);

        // 5. Save salary structure
        SalaryStructure salaryStructure = SalaryStructure.builder()
                .employee(employee)
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .allowances(request.getAllowances())
                .taxPercentage(request.getTaxPercentage())
                .pfPercentage(request.getPfPercentage())
                .build();

        salaryStructureRepository.save(salaryStructure);

        // 6. Response
        return OnboardingResponseDto.builder()
                .empCode(employee.getEmpCode())
                .fullName(employee.getFullName())
                .deptName(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .build();
    }

    /* ================= Helper Methods ================= */

    private String generateUniqueEmpCode() {
        String empCode;
        do {
            empCode = "EMP-" + (10000 + RANDOM.nextInt(90000));
        } while (employeeRepository.existsByEmpCode(empCode));
        return empCode;
    }

    private String buildFullName(String first, String middle, String last) {
        return middle == null || middle.isBlank()
                ? first + " " + last
                : first + " " + middle + " " + last;
    }
}
