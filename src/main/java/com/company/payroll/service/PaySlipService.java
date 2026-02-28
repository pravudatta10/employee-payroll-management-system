package com.company.payroll.service;

import com.company.payroll.entity.Payroll;

public interface PaySlipService {
    byte[] generatePayslipPdf(Payroll payroll);
}
