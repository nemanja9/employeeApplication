package com.example.employeeApplication.repository;

import com.example.employeeApplication.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    List<EmployeeEntity> findAllByTeam_TeamLead_NameContainsIgnoreCase(String name);

    List<EmployeeEntity> findAllByNameContainingIgnoreCase(String name);

    @Query(value = "select e from EmployeeEntity e join TeamEntity t on e = t.teamLead where t.teamLead.name like %:name% and e.team is null")
    List<EmployeeEntity> findOnlyTeamLeadsByNameWithoutTeam(@Param("name") String name);

    @Query(value = "select e from EmployeeEntity e join TeamEntity t on e = t.teamLead where t.teamLead.name like %:name% and e.team is not null")
    List<EmployeeEntity> findOnlyTeamLeadsByNameInATeam(@Param("name") String name);

    List<EmployeeEntity> findAllByTeamIsNullAndNameContainsIgnoreCase(String name);

    List<EmployeeEntity> findAllByTeamIsNotNullAndNameContainsIgnoreCase(String name);

}
