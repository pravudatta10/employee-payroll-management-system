package com.company.payroll.service;

import com.company.payroll.dto.LeaveRequestDto;
import com.company.payroll.dto.LeaveResponseDto;
import com.company.payroll.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {
    LeaveResponseDto applyLeave(LeaveRequestDto leaveRequestDto);

    void approveLeave(Long requestId);

    List<LeaveResponseDto> getAllLeaveRequest(String empCode);
}
