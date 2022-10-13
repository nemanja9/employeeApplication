package com.example.employeeApplication.controller;

import com.example.employeeApplication.dto.EmployeeCreateDto;
import com.example.employeeApplication.dto.EmployeeDto;
import com.example.employeeApplication.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("Employee controller")
@RequestMapping("/api/employees")
@Tag(name = "Employee controller", description = "Public employee operations")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(description = "Returns all employees")
    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok, result")
    })
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(description = "Returns employee based on id")
    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "200", description = "ok, result")
    })
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Operation(description = "Creates an employee based on EmployeeCreateDto")
    @PostMapping("/create")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "201", description = "Employee successfully created")
    })
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeCreateDto newEmployee) {
        return new ResponseEntity<>(employeeService.createEmployee(newEmployee), HttpStatus.CREATED);
    }

    @Operation(description = "Updates an employee based on EmployeeCreateDto")
    @PutMapping("/{id}/update")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Employee/ team not found"),
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted")
    })
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id,
                                                      @RequestBody EmployeeCreateDto updateEmployeeDto) {
        return new ResponseEntity<>(employeeService.updateEmployee(id, updateEmployeeDto), HttpStatus.OK);
    }

    @Operation(description = "Deletes an employee based on employee id")
    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "200", description = "Employee deleted")
    })
    public ResponseEntity<EmployeeDto> deleteEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(employeeService.deleteEmployee(id), HttpStatus.OK);
    }

    @Operation(description = "Search employees based on filters. inATeam- only find employees with or without a team, teamLeadsOnly- only find employees that are team leads or all employees, name- filter for employee name or part of the name.")
    @GetMapping("/search")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok, result")
    })
    public ResponseEntity<List<EmployeeDto>> searchEmployees(@RequestParam(name = "inATeam", required = false) Boolean inATeam,
                                                             @RequestParam(name = "teamLeadsOnly", required = false) Boolean teamLeadsOnly,
                                                             @RequestParam(name = "name", required = false) String name) {
        return new ResponseEntity<>(employeeService.search(inATeam, teamLeadsOnly, name), HttpStatus.OK);
    }
}
