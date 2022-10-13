package com.example.employeeApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateDto {
    private String name;
    private Long teamLeadId;
    private List<Long> employeeIds;
}
