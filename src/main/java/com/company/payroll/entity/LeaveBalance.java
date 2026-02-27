package com.company.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_balance",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employee_id", "leave_year"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer leaveYear;

    @Column(nullable = false, scale = 2)
    private BigDecimal totalPto;

    @Column(nullable = false, scale = 2)
    private BigDecimal usedPto;

    @Column(nullable = false, scale = 2)
    private BigDecimal totalClSl;

    @Column(nullable = false, scale = 2)
    private BigDecimal usedClSl;
}
