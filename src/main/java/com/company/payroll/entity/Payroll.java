package com.company.payroll.entity;

import com.company.payroll.entity.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(
        name = "payroll",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "pay_month"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_month", nullable = false)
    private YearMonth payMonth;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDeductions;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pfAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal leaveDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;

    private LocalDate processedDate;

    @Column(nullable = false, precision = 2, scale = 2)
    private Integer workingDays;

    @Column(nullable = false, precision = 2, scale = 2)
    private Integer paidDays;

    @Column(nullable = false, precision = 2, scale = 2)
    private Integer lopDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status;
}
