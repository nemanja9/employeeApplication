package com.example.employeeApplication.service;

import com.example.employeeApplication.dto.TeamCreateDto;
import com.example.employeeApplication.dto.TeamDto;
import com.example.employeeApplication.entity.EmployeeEntity;
import com.example.employeeApplication.entity.TeamEntity;
import com.example.employeeApplication.exception.ApiExceptionFactory;
import com.example.employeeApplication.repository.EmployeeRepository;
import com.example.employeeApplication.repository.TeamRepository;
import com.example.employeeApplication.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Returns all team dtos.
     *
     * @return all existing team dtos
     */
    public List<TeamDto> getAllTeams() {
        return ModelMapperUtils.mapTeamEntityList(teamRepository.findAll());
    }

    /**
     * Returns a team dto based on team id parameter. If team not found, not found API exception will be thrown.
     *
     * @param id id of the team
     * @return found team dto
     */
    public TeamDto getTeamById(Long id) {
        return ModelMapperUtils.mapTeamEntity(teamRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Team not found")));
    }

    /**
     * Creates a team based on TeamCreateDto. If team lead and/or employees are filled, but do not exist, not found API exception will be thrown.
     * Also, if the team name already exists, conflict API exception will be thrown. Team name is NOT case-sensitive.
     * Team can be saved without employees and/or team lead. Team id will be auto-generated and returned.
     *
     * @param teamCreateDto data for creating the new team
     * @return returns the newly created team dto
     */
    public TeamDto createTeam(TeamCreateDto teamCreateDto) {
        EmployeeEntity teamLead = null;
        Set<EmployeeEntity> employeesInTeam = new HashSet<>();
        if (teamCreateDto.getTeamLeadId() != null) {
            teamLead = employeeRepository.findById(teamCreateDto.getTeamLeadId()).orElseThrow(() -> ApiExceptionFactory.notFound("Team lead not found!"));
        }
        if (teamCreateDto.getEmployeeIds() != null && !teamCreateDto.getEmployeeIds().isEmpty()) {
            employeesInTeam = new HashSet<>(employeeRepository.findAllById(teamCreateDto.getEmployeeIds()));
            // if sizes differ, not all employees exist in the db
            if (employeesInTeam.size() != teamCreateDto.getEmployeeIds().size()) {
                throw ApiExceptionFactory.notFound("Some employees not found!");
            }
        }
        Optional<TeamEntity> existingName = teamRepository.findAllByNameEqualsIgnoreCase(teamCreateDto.getName());
        if (existingName.isPresent()) {
            throw ApiExceptionFactory.conflict("Team with given name already exists!");
        }

        TeamEntity toSave = TeamEntity.builder()
                .teamLead(teamLead)
                .name(teamCreateDto.getName())
                .employeesInTeam(employeesInTeam)
                .build();

        TeamEntity saved = teamRepository.save(toSave);

        //switching employees to new team
        employeesInTeam.forEach(x -> x.setTeam(saved));
        employeeRepository.saveAll(employeesInTeam);
        return ModelMapperUtils.mapTeamEntity(saved);
    }

    /**
     * Updates a team based on TeamCreateDto, and based on the id param. If the team cannot be found with given id, not found API exception will be thrown.
     * If team lead and/or employees are filled, but do not exist, not found API exception will be thrown.
     * Also, if the team name already exists in some other team, conflict API exception will be thrown. Team name is NOT case-sensitive.
     * Team can be saved without employees and/or team lead.
     *
     * @param updateTeamDto data for updating the team
     * @param id            id of the team to be updated
     * @return returns the updated team dto
     */
    public TeamDto updateTeam(Long id, TeamCreateDto updateTeamDto) {
        // checking if updating an existing team
        TeamEntity teamEntity = teamRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Team with given ID doesn't exist!"));

        // checking if new name already exists
        Optional<TeamEntity> existingName = teamRepository.findAllByNameEqualsIgnoreCase(updateTeamDto.getName());
        if (existingName.isPresent() && !existingName.get().getId().equals(id)) {
            throw ApiExceptionFactory.conflict("Team with given name already exists!");
        }

        // checking if team lead changed
        if (updateTeamDto.getTeamLeadId() != null) {
            teamEntity.setTeamLead(employeeRepository.findById(updateTeamDto.getTeamLeadId()).orElseThrow(() -> ApiExceptionFactory.notFound("Team lead not found!")));
        } else {
            teamEntity.setTeamLead(null);
        }
        Set<EmployeeEntity> employeesInTeam = new HashSet<>();
        if (updateTeamDto.getEmployeeIds() != null && !updateTeamDto.getEmployeeIds().isEmpty()) {
            employeesInTeam = new HashSet<>(employeeRepository.findAllById(updateTeamDto.getEmployeeIds()));
            TeamEntity finalTeamEntity = teamEntity;
            employeesInTeam.forEach(x -> x.setTeam(finalTeamEntity));
            // if sizes differ, not all employees exist in the db
            if (employeesInTeam.size() != updateTeamDto.getEmployeeIds().size()) {
                throw ApiExceptionFactory.notFound("Some employees not found!");
            }
        } else {
            // else: the updated team doesn't have any employees, removing all people from the team
            teamEntity.getEmployeesInTeam().forEach(x -> x.setTeam(null));
        }
        teamEntity.setEmployeesInTeam(employeesInTeam);
        employeeRepository.saveAll(teamEntity.getEmployeesInTeam());

        // updating the team name if it is not empty
        if (updateTeamDto.getName() != null && !updateTeamDto.getName().isEmpty()) {
            teamEntity.setName(updateTeamDto.getName());
        }
        teamEntity = teamRepository.save(teamEntity);
        return ModelMapperUtils.mapTeamEntity(teamEntity);
    }

    /**
     * Deletes a team based on team id. If the team cannot be found with given id, not found API exception will be thrown.
     *
     * @param id id of the team to be deleted
     * @return the deleted team
     */
    public TeamDto deleteTeam(Long id) {
        // checking if team exists
        TeamEntity teamEntity = teamRepository.findById(id).orElseThrow(() -> ApiExceptionFactory.notFound("Team with given ID doesn't exist!"));

        // updating the employees
        teamEntity.getEmployeesInTeam().forEach(x -> x.setTeam(null));
        employeeRepository.saveAll(teamEntity.getEmployeesInTeam());

        teamRepository.delete(teamEntity);
        return ModelMapperUtils.mapTeamEntity(teamEntity);
    }
}
