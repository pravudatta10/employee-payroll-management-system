package com.company.payroll.dto;

import com.company.payroll.entity.enums.LeaveStatus;
import com.company.payroll.entity.enums.LeaveType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponseDto {
    private Long id;
    private String empCode;
    private String firstName;
    private String middleName;
    private String lastName;
    private LeaveType leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int totalDays;
    private LeaveStatus status;
}
