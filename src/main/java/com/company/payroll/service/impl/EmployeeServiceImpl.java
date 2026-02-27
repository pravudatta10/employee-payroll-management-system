package com.company.payroll.service.impl;

import com.company.payroll.dto.EmployeeResponseDto;
import com.company.payroll.dto.OnboardingRequestDto;
import com.company.payroll.dto.OnboardingResponseDto;
import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveBalance;
import com.company.payroll.entity.SalaryStructure;
import com.company.payroll.exception.EmployeeNotFoundException;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.LeaveBalanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.SalaryStructureRepository;
import com.company.payroll.service.EmployeeService;
import com.company.payroll.util.PayrollConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    /* ================= PUBLIC METHODS ================= */

    @Override
    public OnboardingResponseDto onBoardNewEmployee(OnboardingRequestDto request) {

        // Validate duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(PayrollConstants.EMAIL_ALREADY_EXIST);
        }

        Employee employee = Employee.builder()
                .empCode(generateUniqueEmpCode())
                .active(true)
                .build();

        return saveOrUpdateEmployee(employee, request);
    }

    @Override
    public OnboardingResponseDto updateEmployeeRecord(
            String empCode,
            OnboardingRequestDto request) {

        Employee employee = employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(PayrollConstants.EMPLOYEE_NOT_FOUND + empCode)
                );

        return saveOrUpdateEmployee(employee, request);
    }

    @Override
    public void deleteEmployeeRecord(String empCode) {

        Employee employee = employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(PayrollConstants.EMPLOYEE_NOT_FOUND + empCode)
                );
        if (!employee.getActive()) {
            throw new IllegalStateException("Employee already inactive");
        }
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllActiveEmployees() {

        List<Employee> activeEmployees =
                employeeRepository.findByActiveTrueOrderByCreatedAtDesc();

        return activeEmployees.stream()
                .map(emp -> {

                    SalaryStructure salary = salaryStructureRepository
                            .findByEmployee(emp)
                            .orElse(null);

                    return EmployeeResponseDto.builder()
                            .id(emp.getId())
                            .empCode(emp.getEmpCode())
                            .email(emp.getEmail())
                            .firstName(emp.getFirstName())
                            .middleName(emp.getMiddleName())
                            .lastName(emp.getLastName())
                            .department(emp.getDepartment())
                            .designation(emp.getDesignation())
                            .joiningDate(emp.getJoiningDate())

                            // Salary (null-safe)
                            .basicSalary(salary != null ? salary.getBasicSalary() : null)
                            .hra(salary != null ? salary.getHra() : null)
                            .allowances(salary != null ? salary.getAllowances() : null)
                            .taxPercentage(salary != null ? salary.getTaxPercentage() : null)
                            .pfPercentage(salary != null ? salary.getPfPercentage() : null)

                            .build();
                })
                .toList();
    }

    @Override
    public EmployeeResponseDto getActiveEmployeeByEmpCode(String empCode) {
        Employee emp = employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(PayrollConstants.EMPLOYEE_NOT_FOUND + empCode)
                );
        SalaryStructure salary = salaryStructureRepository
                .findByEmployee(emp)
                .orElse(null);
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployee(emp)
                .orElse(null);
        return EmployeeResponseDto.builder()
                .id(emp.getId())
                .empCode(emp.getEmpCode())
                .firstName(emp.getFirstName())
                .middleName(emp.getMiddleName())
                .lastName(emp.getLastName())
                .department(emp.getDepartment())
                .designation(emp.getDesignation())
                .joiningDate(emp.getJoiningDate())
                .email(emp.getEmail())
                // Salary (null-safe)
                .basicSalary(salary != null ? salary.getBasicSalary() : null)
                .hra(salary != null ? salary.getHra() : null)
                .allowances(salary != null ? salary.getAllowances() : null)
                .taxPercentage(salary != null ? salary.getTaxPercentage() : null)
                .pfPercentage(salary != null ? salary.getPfPercentage() : null)
                //leave balance
                .totalPTO(leaveBalance.getTotalPto())
                .usedPTO(leaveBalance.getUsedPto())
                .totalCLSL(leaveBalance.getTotalClSl())
                .usedCLSL(leaveBalance.getUsedClSl())
                .build();
    }



    /* ================= COMMON SAVE / UPDATE ================= */

    private OnboardingResponseDto saveOrUpdateEmployee(
            Employee employee,
            OnboardingRequestDto request) {

        // Update employee fields
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());

        Employee savedEmployee = employeeRepository.save(employee);

        // Salary Structure
        SalaryStructure salaryStructure = salaryStructureRepository
                .findByEmployee(savedEmployee)
                .orElse(
                        SalaryStructure.builder()
                                .employee(savedEmployee)
                                .build()
                );

        salaryStructure.setBasicSalary(request.getBasicSalary());
        salaryStructure.setHra(request.getHra());
        salaryStructure.setAllowances(request.getAllowances());
        salaryStructure.setTaxPercentage(request.getTaxPercentage());
        salaryStructure.setPfPercentage(request.getPfPercentage());

        salaryStructureRepository.save(salaryStructure);
        Map<String, Double> leaveRemaining = calculateFromJoiningDateToYearEnd(request.getJoiningDate());
        LeaveBalance leave = LeaveBalance.builder()
                .employee(employee)
                .leaveYear(LocalDate.now().getYear())
                .totalPto(BigDecimal.valueOf(leaveRemaining.get("PTO")))
                .totalClSl(BigDecimal.valueOf(leaveRemaining.get("clSl")))
                .usedPto(BigDecimal.valueOf(0.0))
                .usedClSl(BigDecimal.valueOf(0.0))
                .build();
        leaveBalanceRepository.save(leave);
        return buildResponse(savedEmployee);
    }

    /* ================= HELPERS ================= */

    private String generateUniqueEmpCode() {
        String empCode;
        do {
            empCode = "EMP-" + (10000 + RANDOM.nextInt(90000));
        } while (employeeRepository.existsByEmpCode(empCode));
        return empCode;
    }

    private String buildFullName(String first, String middle, String last) {
        return (middle == null || middle.isBlank())
                ? first + " " + last
                : first + " " + middle + " " + last;
    }

    private OnboardingResponseDto buildResponse(Employee employee) {
        return OnboardingResponseDto.builder()
                .empCode(employee.getEmpCode())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastName(employee.getLastName())
                .deptName(employee.getDepartment())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .build();
    }


    public Map<String, Double> calculateFromJoiningDateToYearEnd(LocalDate joiningDate) {
        Map<String, Double> totalLeave = new HashMap<>();
        LocalDate effectiveJoining = joiningDate.getDayOfMonth() <= 15
                ? joiningDate
                : joiningDate.plusMonths(1).withDayOfMonth(1);

        LocalDate yearEnd = LocalDate.of(joiningDate.getYear(), 12, 31);

        long completedMonths = ChronoUnit.MONTHS.between(
                YearMonth.from(effectiveJoining),
                YearMonth.from(yearEnd)
        );

        Double pto = completedMonths * PayrollConstants.PTO_PER_MONTH;
        Double clSl = completedMonths * PayrollConstants.CLSL_PER_MONTH;
        totalLeave.put("PTO", pto);
        totalLeave.put("clSl", clSl);
        return totalLeave;
    }
}
