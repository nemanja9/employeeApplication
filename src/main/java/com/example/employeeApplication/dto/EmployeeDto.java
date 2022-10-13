package com.example.employeeApplication.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String name;
    @JsonBackReference
    private TeamDto team;
}
