package com.example.employeeApplication.service;

import com.example.employeeApplication.dto.EmployeeCreateDto;
import com.example.employeeApplication.dto.EmployeeDto;
import com.example.employeeApplication.entity.EmployeeEntity;
import com.example.employeeApplication.entity.TeamEntity;
import com.example.employeeApplication.exception.ApiExceptionFactory;
import com.example.employeeApplication.repository.EmployeeRepository;
import com.example.employeeApplication.repository.TeamRepository;
import com.example.employeeApplication.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;

    /**
     * Returns all employee dtos
     *
     * @return all existing employee dtos
     */
    public List<EmployeeDto> getAllEmployees() {
        return ModelMapperUtils.mapEmployeeEntityList(employeeRepository.findAll());
    }

    /**
     * Returns an employee dto based on employee id parameter. If employee not found, not found API exception will be thrown.
     *
     * @param id id of the employee
     * @return found employee dto
     */
    public EmployeeDto getEmployeeById(Long id) {
        return ModelMapperUtils.mapEmployeeEntity(employeeRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Employee with given id not found!")));
    }

    /**
     * Creates an employee based on EmployeeCreateDto. If team is filled but does not exist, not found API exception will be thrown.
     * Employee can be saved without a team. Employee id will be auto-generated and returned.
     *
     * @param newEmployee data for creating the new employee
     * @return returns the newly created employee dto
     */
    public EmployeeDto createEmployee(EmployeeCreateDto newEmployee) {
        TeamEntity teamToSave = null;
        if (newEmployee != null && newEmployee.getTeamId() != null) {
            teamToSave = teamRepository.findById(newEmployee.getTeamId()).orElseThrow(() -> ApiExceptionFactory.notFound("Selected team doesn't exist!"));
        }
        EmployeeEntity employeeToSave = ModelMapperUtils.map(newEmployee, EmployeeEntity.class);
        employeeToSave.setTeam(teamToSave);

        return ModelMapperUtils.map(employeeRepository.save(employeeToSave), EmployeeDto.class);
    }

    /**
     * Updates an employee based on EmployeeCreateDto. If team is filled but does not exist, not found API exception will be thrown.
     * Employee can be saved without a team.
     *
     * @param updateEmployeeDto data for updating the employee
     * @param id                id of the employee to be updated
     * @return returns the updated employee dto
     */
    public EmployeeDto updateEmployee(Long id, EmployeeCreateDto updateEmployeeDto) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Employee with given id not found!"));

        TeamEntity teamEntity = null;
        if (updateEmployeeDto.getTeamId() != null) {
            teamEntity = teamRepository.findById(updateEmployeeDto.getTeamId()).orElseThrow(() -> ApiExceptionFactory.notFound("Selected team doesn't exist!"));
        }
        employeeEntity.setTeam(teamEntity);
        if (updateEmployeeDto.getName() != null && !updateEmployeeDto.getName().isEmpty()) {
            employeeEntity.setName(updateEmployeeDto.getName());
        }

        employeeEntity = employeeRepository.save(employeeEntity);
        return ModelMapperUtils.mapEmployeeEntity(employeeEntity);
    }


    /**
     * Deletes an employee based on employee id. If the employee cannot be found, not found API exception will be thrown,
     *
     * @param id id of the employee to be deleted
     * @return the deleted employee
     */
    public EmployeeDto deleteEmployee(Long id) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Employee with given id not found!"));
        // clearing all teams lead by employee to be deleted
        employeeEntity.getTeamsLed().forEach(x -> x.setTeamLead(null));
        teamRepository.saveAll(employeeEntity.getTeamsLed());
        employeeRepository.delete(employeeEntity);
        return ModelMapperUtils.mapEmployeeEntity(employeeEntity);
    }

    /**
     * Searches for employees based on 3 optional filters. Any filter can be null and in that case the results won't be filtered based on the null filters.
     *
     * @param inATeam       filter for employees that are in a team or not. True- only employees in a team, false- only employees without a team.
     * @param teamLeadsOnly filters for employees that are team leads. True- only team leads, false- all employees
     * @param name          filter for employee name or part of the name
     * @return list of found employees based on the search parameters
     */
    public List<EmployeeDto> search(Boolean inATeam, Boolean teamLeadsOnly, String name) {
        if (name == null) {
            name = "";
        }
        List<EmployeeEntity> results;
        if (teamLeadsOnly != null && teamLeadsOnly) {
            if (inATeam != null && inATeam) {
                results = employeeRepository.findOnlyTeamLeadsByNameInATeam(name);
            } else if (inATeam != null) {
                results = employeeRepository.findOnlyTeamLeadsByNameWithoutTeam(name);
            } else {
                results = employeeRepository.findAllByTeam_TeamLead_NameContainsIgnoreCase(name);
            }
        } else {
            if (inATeam != null && inATeam) {
                results = employeeRepository.findAllByTeamIsNotNullAndNameContainsIgnoreCase(name);
            } else if (inATeam != null) {
                results = employeeRepository.findAllByTeamIsNullAndNameContainsIgnoreCase(name);
            } else {
                results = employeeRepository.findAllByNameContainingIgnoreCase(name);
            }
        }
        return ModelMapperUtils.mapEmployeeEntityList(results);
    }
}
