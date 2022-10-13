package com.example.employeeApplication.utils;

import com.example.employeeApplication.dto.EmployeeDto;
import com.example.employeeApplication.dto.TeamDto;
import com.example.employeeApplication.entity.EmployeeEntity;
import com.example.employeeApplication.entity.TeamEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelMapperUtils {

    private static final ModelMapper modelMapper;
    private static final MatchingStrategy defaultMatchingStrategy = MatchingStrategies.STRICT;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(defaultMatchingStrategy);
    }


    /**
     * Maps entity into an object
     *
     * @param entity   source
     * @param outClass destination
     * @return mapped object
     */
    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }


    public static List<EmployeeDto> mapEmployeeEntityList(List<EmployeeEntity> entities) {
        return entities.stream().map(ModelMapperUtils::mapEmployeeEntity).collect(Collectors.toList());
    }

    /**
     * Maps employeeEntity into employeeDto
     *
     * @param employeeEntity source
     * @return mapped employeeDto
     */
    public static EmployeeDto mapEmployeeEntity(EmployeeEntity employeeEntity) {
        EmployeeDto employeeDto = EmployeeDto.builder()
                .id(employeeEntity.getId())
                .name(employeeEntity.getName())
                .build();
        if (employeeEntity.getTeam() != null) {
            employeeDto.setTeam(ModelMapperUtils.map(employeeEntity.getTeam(), TeamDto.class));
        }
        return employeeDto;
    }


    /**
     * Maps teamEntity into teamDto
     *
     * @param teamEntity source
     * @return mapped teamDto
     */
    public static TeamDto mapTeamEntity(TeamEntity teamEntity) {
        TeamDto teamDto = TeamDto.builder()
                .name(teamEntity.getName())
                .id(teamEntity.getId())
                .build();

        if (teamEntity.getEmployeesInTeam() != null) {
            teamDto.setEmployees(mapEmployeeEntityList(new LinkedList<>(teamEntity.getEmployeesInTeam())));
        }
        if (teamEntity.getTeamLead() != null) {
            teamDto.setTeamLead(ModelMapperUtils.map(teamEntity.getTeamLead(), EmployeeDto.class));
        }
        return teamDto;
    }

    /**
     * Maps list of teamEntities into a list od teamDtos
     *
     * @param entities source
     * @return mapped list of teamDtos
     */
    public static List<TeamDto> mapTeamEntityList(List<TeamEntity> entities) {
        return entities.stream().map(ModelMapperUtils::mapTeamEntity).collect(Collectors.toList());
    }
}
