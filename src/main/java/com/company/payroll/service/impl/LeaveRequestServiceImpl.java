package com.company.payroll.service.impl;

import com.company.payroll.dto.LeaveRequestDto;
import com.company.payroll.dto.LeaveResponseDto;
import com.company.payroll.entity.Employee;
import com.company.payroll.entity.LeaveBalance;
import com.company.payroll.entity.LeaveRequest;
import com.company.payroll.entity.enums.LeaveStatus;
import com.company.payroll.entity.enums.LeaveType;
import com.company.payroll.exception.EmployeeNotFoundException;
import com.company.payroll.repository.EmployeeRepository;
import com.company.payroll.repository.LeaveBalanceRepository;
import com.company.payroll.repository.LeaveRequestRepository;
import com.company.payroll.service.LeaveRequestService;
import com.company.payroll.util.PayrollConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public LeaveResponseDto applyLeave(LeaveRequestDto dto) {

        Employee employee = employeeRepository.findByEmpCode(dto.getEmpCode())
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                PayrollConstants.EMPLOYEE_NOT_FOUND + dto.getEmpCode()
                        )
                );

        int year = dto.getFromDate().getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveYear(employee, year)
                .orElseThrow(() ->
                        new IllegalArgumentException("Leave balance not initialized for year " + year)
                );

        long days = calculateBusinessDays(dto.getFromDate(), dto.getToDate());

        validateSufficientBalance(dto.getLeaveType(), balance, days);

        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .leaveType(dto.getLeaveType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .totalDays((int) days)
                .status(LeaveStatus.PENDING)
                .appliedDate(LocalDate.now())
                .reason(dto.getReason())
                .build();

        leaveRequestRepository.save(request);

        return LeaveResponseDto.builder()
                .empCode(employee.getEmpCode())
                .leaveType(dto.getLeaveType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .totalDays((int) days)
                .status(request.getStatus())
                .build();
    }

    private void validateSufficientBalance(LeaveType type,
                                           LeaveBalance balance,
                                           long requestedDays) {

        BigDecimal remaining;

        if (type == LeaveType.PTO) {
            remaining = balance.getTotalPto()
                    .subtract(balance.getUsedPto());
        } else {
            remaining = balance.getTotalClSl()
                    .subtract(balance.getUsedClSl());
        }

        if (remaining.compareTo(BigDecimal.valueOf(requestedDays)) < 0) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }
    }

    @Transactional
    public void approveLeave(Long requestId) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow();

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveYear(
                        request.getEmployee(),
                        request.getFromDate().getYear()
                )
                .orElseThrow();

        BigDecimal days = BigDecimal.valueOf(request.getTotalDays());

        if (request.getLeaveType() == LeaveType.PTO) {
            balance.setUsedPto(balance.getUsedPto().add(days));
        } else {
            balance.setUsedClSl(balance.getUsedClSl().add(days));
        }

        request.setStatus(LeaveStatus.APPROVED);

        leaveRequestRepository.save(request);
        leaveBalanceRepository.save(balance);
    }

    @Transactional
    public List<LeaveResponseDto> getAllLeaveRequest(String empCode) {

        List<LeaveRequest> leaves;

        if (empCode != null) {
            leaves = leaveRequestRepository.findByEmployeeEmpCode(empCode);
        } else {
            leaves = leaveRequestRepository.findAll();
        }

        return leaves.stream()
                .map(this::mapToResponse)
                .toList();
    }


    private long calculateBusinessDays(LocalDate start, LocalDate end) {

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("To date cannot be before from date");
        }

        long days = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            if (!(date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY)) {

                days++;
            }
        }

        return days;
    }

    private LeaveResponseDto mapToResponse(LeaveRequest leave) {
        return LeaveResponseDto.builder()
                .id(leave.getId())
                .firstName(leave.getEmployee().getFirstName())
                .middleName(leave.getEmployee().getMiddleName())
                .lastName(leave.getEmployee().getLastName())
                .empCode(leave.getEmployee().getEmpCode())
                .leaveType(leave.getLeaveType())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .totalDays(leave.getTotalDays())
                .status(leave.getStatus())
                .build();
    }

}
