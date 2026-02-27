package com.company.payroll.controller;


import com.company.payroll.dto.LeaveRequestDto;
import com.company.payroll.dto.LeaveResponseDto;
import com.company.payroll.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestService leaveService;

    /**
     * Employee applies for leave
     */
    @PostMapping
    public ResponseEntity<LeaveResponseDto> applyLeave(@RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveResponseDto response = leaveService.applyLeave(leaveRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * HR approves leave
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<Void> approveLeave(@PathVariable Long requestId) {
        leaveService.approveLeave(requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get leave requests
     * If empCode provided → employee view
     * If empCode null → HR default (e.g. pending)
     */
    @GetMapping
    public ResponseEntity<List<LeaveResponseDto>> getLeaveRequests(@RequestParam(required = false) String empCode) {
        return ResponseEntity.ok(leaveService.getAllLeaveRequest(empCode));
    }

}
