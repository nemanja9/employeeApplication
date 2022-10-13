package com.example.employeeApplication.repository;

import com.example.employeeApplication.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    Optional<TeamEntity> findAllByNameEqualsIgnoreCase(String name);

}
