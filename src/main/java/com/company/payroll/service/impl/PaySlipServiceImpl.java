package com.company.payroll.service.impl;

import com.company.payroll.entity.Employee;
import com.company.payroll.entity.Payroll;
import com.company.payroll.entity.enums.PayrollStatus;
import com.company.payroll.service.PaySlipService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaySlipServiceImpl implements PaySlipService {


    @Override
    public byte[] generatePayslipPdf(Payroll payroll) {

        if (payroll.getStatus() != PayrollStatus.GENERATED
                && payroll.getStatus() != PayrollStatus.PAID) {

            throw new IllegalStateException("Payroll not generated yet");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ================= HEADER =================

            document.add(new Paragraph("TechNova Systems Pvt Ltd")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Bangalore, India")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Pay Slip for "
                    + payroll.getPayMonth()
                    .format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                    .setBold()
                    .setMarginBottom(20)
                    .setTextAlignment(TextAlignment.CENTER));

            // ================= EMPLOYEE DETAILS TABLE =================

            Table empTable = new Table(UnitValue.createPercentArray(4))
                    .useAllAvailableWidth();

            empTable.addCell(createCell("Employee ID"));
            empTable.addCell(createCell(payroll.getEmployee().getId().toString()));
            empTable.addCell(createCell("Designation"));
            empTable.addCell(createCell(payroll.getEmployee().getDesignation()));

            empTable.addCell(createCell("Employee Name"));
            empTable.addCell(createCell(buildFullName(payroll.getEmployee())));
            empTable.addCell(createCell("Date of Joining"));
            empTable.addCell(createCell(
                    payroll.getEmployee().getJoiningDate().toString()));

            document.add(empTable);
            document.add(new Paragraph("\n"));

            // ================= SUMMARY TABLE =================

            Table summaryTable = new Table(UnitValue.createPercentArray(4))
                    .useAllAvailableWidth();

            summaryTable.addCell(createCell("Gross Salary"));
            summaryTable.addCell(createCell("₹ " + payroll.getGrossSalary()));

            summaryTable.addCell(createCell("Working Days"));
            summaryTable.addCell(createCell(String.valueOf(payroll.getWorkingDays())));

            summaryTable.addCell(createCell("LOP Days"));
            summaryTable.addCell(createCell(String.valueOf(payroll.getLopDays())));

            summaryTable.addCell(createCell("Paid Days"));
            summaryTable.addCell(createCell(String.valueOf(payroll.getPaidDays())));

            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // ================= EARNINGS & DEDUCTIONS =================

            Table salaryTable = new Table(UnitValue.createPercentArray(4))
                    .useAllAvailableWidth();

            salaryTable.addHeaderCell(createHeaderCell("Earnings"));
            salaryTable.addHeaderCell(createHeaderCell("Amount"));
            salaryTable.addHeaderCell(createHeaderCell("Deductions"));
            salaryTable.addHeaderCell(createHeaderCell("Amount"));

            salaryTable.addCell(createCell("Basic"));
            salaryTable.addCell(createCell("₹ " + payroll.getGrossSalary()));
            salaryTable.addCell(createCell("PF"));
            salaryTable.addCell(createCell("₹ " + payroll.getPfAmount()));

            salaryTable.addCell(createCell("HRA"));
            salaryTable.addCell(createCell("Included"));
            salaryTable.addCell(createCell("Tax"));
            salaryTable.addCell(createCell("₹ " + payroll.getTaxAmount()));

            salaryTable.addCell(createCell("Leave Deduction"));
            salaryTable.addCell(createCell("₹ 0"));
            salaryTable.addCell(createCell("Leave Deduction"));
            salaryTable.addCell(createCell("₹ " + payroll.getLeaveDeduction()));

            salaryTable.addCell(createHeaderCell("Total Earnings"));
            salaryTable.addCell(createHeaderCell("₹ " + payroll.getGrossSalary()));
            salaryTable.addCell(createHeaderCell("Total Deductions"));
            salaryTable.addCell(createHeaderCell("₹ " + payroll.getTotalDeductions()));

            document.add(salaryTable);
            document.add(new Paragraph("\n"));

            // ================= NET SALARY =================

            document.add(new Paragraph("Net Salary: ₹ " + payroll.getNetSalary())
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating payslip PDF", e);
        }
    }

    private Cell createCell(String value) {
        return new Cell()
                .add(new Paragraph(value))
                .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
    }

    private Cell createHeaderCell(String value) {
        return new Cell()
                .add(new Paragraph(value).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
    }

    private String buildFullName(Employee employee) {
        return Stream.of(
                        employee.getFirstName(),
                        employee.getMiddleName(),
                        employee.getLastName()
                )
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}
