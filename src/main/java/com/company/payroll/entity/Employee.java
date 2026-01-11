package com.company.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "employee",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "emp_code"),
                @UniqueConstraint(columnNames = "email")
        }
)
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_code", nullable = false, length = 20)
    private String empCode;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(nullable = false, length = 50)
    private String designation;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private Boolean active = true;
}
