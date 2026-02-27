package com.company.payroll.config;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveBalance;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.SalaryStructure;
import com.company.payroll.entity.enums.LeaveStatus;
import com.company.payroll.entity.enums.LeaveType;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.LeaveBalanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.repository.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public void run(String... args) {

        if (employeeRepository.count() > 0) {
            return; // Prevent duplicate inserts
        }

        Employee e1 = Employee.builder()
                .empCode("EMP001")
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul.sharma@company.com")
                .department("Engineering")
                .designation("Software Engineer")
                .joiningDate(LocalDate.of(2022, 3, 15))
                .active(true)
                .build();

        Employee e2 = Employee.builder()
                .empCode("EMP002")
                .firstName("Ananya")
                .lastName("Iyer")
                .email("ananya.iyer@company.com")
                .department("Engineering")
                .designation("Senior Software Engineer")
                .joiningDate(LocalDate.of(2021, 7, 5))
                .active(true)
                .build();

        Employee e3 = Employee.builder()
                .empCode("EMP003")
                .firstName("Amit")
                .lastName("Verma")
                .email("amit.verma@company.com")
                .department("HR")
                .designation("HR Manager")
                .joiningDate(LocalDate.of(2020, 1, 20))
                .active(true)
                .build();

        Employee e4 = Employee.builder()
                .empCode("EMP004")
                .firstName("Priya")
                .lastName("Nair")
                .email("priya.nair@company.com")
                .department("HR")
                .designation("HR Executive")
                .joiningDate(LocalDate.of(2022, 11, 10))
                .active(true)
                .build();

        Employee e5 = Employee.builder()
                .empCode("EMP005")
                .firstName("Suresh")
                .lastName("Reddy")
                .email("suresh.reddy@company.com")
                .department("Admin")
                .designation("Operations Lead")
                .joiningDate(LocalDate.of(2019, 6, 1))
                .active(true)
                .build();

        List<Employee> employees = employeeRepository.saveAll(
                List.of(e1, e2, e3, e4, e5)
        );

        salaryStructureRepository.saveAll(List.of(

                SalaryStructure.builder()
                        .employee(employees.get(0))
                        .basicSalary(new BigDecimal("40000"))
                        .hra(new BigDecimal("15000"))
                        .allowances(new BigDecimal("5000"))
                        .taxPercentage(new BigDecimal("10"))
                        .pfPercentage(new BigDecimal("12"))
                        .build(),

                SalaryStructure.builder()
                        .employee(employees.get(1))
                        .basicSalary(new BigDecimal("65000"))
                        .hra(new BigDecimal("25000"))
                        .allowances(new BigDecimal("10000"))
                        .taxPercentage(new BigDecimal("15"))
                        .pfPercentage(new BigDecimal("12"))
                        .build(),

                SalaryStructure.builder()
                        .employee(employees.get(2))
                        .basicSalary(new BigDecimal("55000"))
                        .hra(new BigDecimal("20000"))
                        .allowances(new BigDecimal("8000"))
                        .taxPercentage(new BigDecimal("12"))
                        .pfPercentage(new BigDecimal("10"))
                        .build(),

                SalaryStructure.builder()
                        .employee(employees.get(3))
                        .basicSalary(new BigDecimal("35000"))
                        .hra(new BigDecimal("12000"))
                        .allowances(new BigDecimal("4000"))
                        .taxPercentage(new BigDecimal("8"))
                        .pfPercentage(new BigDecimal("10"))
                        .build(),

                SalaryStructure.builder()
                        .employee(employees.get(4))
                        .basicSalary(new BigDecimal("70000"))
                        .hra(new BigDecimal("30000"))
                        .allowances(new BigDecimal("12000"))
                        .taxPercentage(new BigDecimal("18"))
                        .pfPercentage(new BigDecimal("12"))
                        .build()
        ));
        leaveRequestRepository.saveAll(List.of(

                LeaveRequest.builder()
                        .employee(employees.get(0))
                        .leaveType(LeaveType.CLSL)
                        .fromDate(LocalDate.of(2026, 1, 10))
                        .toDate(LocalDate.of(2026, 1, 11))
                        .totalDays(2)
                        .status(LeaveStatus.APPROVED)
                        .appliedDate(LocalDate.of(2026, 1, 5))
                        .reason("Sick Leave")
                        .build(),

                LeaveRequest.builder()
                        .employee(employees.get(1))
                        .leaveType(LeaveType.PTO)
                        .fromDate(LocalDate.of(2026, 2, 3))
                        .toDate(LocalDate.of(2026, 2, 3))
                        .totalDays(1)
                        .status(LeaveStatus.APPROVED)
                        .appliedDate(LocalDate.of(2026, 2, 2))
                        .reason("PTO")
                        .build(),

                LeaveRequest.builder()
                        .employee(employees.get(2))
                        .leaveType(LeaveType.PTO)
                        .fromDate(LocalDate.of(2026, 3, 15))
                        .toDate(LocalDate.of(2026, 3, 19))
                        .totalDays(5)
                        .status(LeaveStatus.PENDING)
                        .appliedDate(LocalDate.of(2026, 3, 10))
                        .reason("PTO")
                        .build(),

                LeaveRequest.builder()
                        .employee(employees.get(3))
                        .leaveType(LeaveType.CLSL)
                        .fromDate(LocalDate.of(2026, 4, 5))
                        .toDate(LocalDate.of(2026, 4, 5))
                        .totalDays(1)
                        .status(LeaveStatus.REJECTED)
                        .appliedDate(LocalDate.of(2026, 4, 3))
                        .reason("Sick Leave")
                        .build()
        ));
        leaveBalanceRepository.saveAll(
                List.of(

                        LeaveBalance.builder()
                                .employee(employees.get(0))
                                .leaveYear(LocalDate.now().getYear())
                                .totalPto(BigDecimal.valueOf(18.0))
                                .usedPto(BigDecimal.valueOf(2.0))
                                .totalClSl(BigDecimal.valueOf(12.0))
                                .usedClSl(BigDecimal.valueOf(2.0))
                                .build(),

                        LeaveBalance.builder()
                                .employee(employees.get(1))
                                .leaveYear(LocalDate.now().getYear())
                                .totalPto(BigDecimal.valueOf(18.0))
                                .usedPto(BigDecimal.valueOf(5.0))
                                .totalClSl(BigDecimal.valueOf(12.0))
                                .usedClSl(BigDecimal.valueOf(6.0))
                                .build(),

                        LeaveBalance.builder()
                                .employee(employees.get(2))
                                .leaveYear(LocalDate.now().getYear())
                                .totalPto(BigDecimal.valueOf(18.0))
                                .usedPto(BigDecimal.valueOf(3.0))
                                .totalClSl(BigDecimal.valueOf(12.0))
                                .usedClSl(BigDecimal.valueOf(5.0))
                                .build(),
                        LeaveBalance.builder()
                                .employee(employees.get(3))
                                .leaveYear(LocalDate.now().getYear())
                                .totalPto(BigDecimal.valueOf(18.0))
                                .usedPto(BigDecimal.valueOf(4.0))
                                .totalClSl(BigDecimal.valueOf(12.0))
                                .usedClSl(BigDecimal.valueOf(2.0))
                                .build(),
                        LeaveBalance.builder()
                                .employee(employees.get(4))
                                .leaveYear(LocalDate.now().getYear())
                                .totalPto(BigDecimal.valueOf(18.0))
                                .usedPto(BigDecimal.valueOf(7.0))
                                .totalClSl(BigDecimal.valueOf(12.0))
                                .usedClSl(BigDecimal.valueOf(2.0))
                                .build()
                )
        );
    }
}
