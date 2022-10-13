package com.example.employeeApplication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeCreateDto {
    private String name;
    private Long teamId;
}
