package com.example.employeeApplication.controller;

import com.example.employeeApplication.dto.TeamCreateDto;
import com.example.employeeApplication.dto.TeamDto;
import com.example.employeeApplication.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("Team controller")
@RequestMapping("/api/teams")
@Tag(name = "Team controller", description = "Public team operations")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(description = "Returns all teams")
    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok, result")
    })
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation(description = "Returns team based on id")
    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "200", description = "ok, result")
    })
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        return new ResponseEntity<>(teamService.getTeamById(id), HttpStatus.OK);
    }

    @Operation(description = "Creates a team based on TeamCreateDto")
    @PostMapping("/create")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Employees and/or team lead not found"),
            @ApiResponse(responseCode = "409", description = "Team name already exists"),
            @ApiResponse(responseCode = "201", description = "Team successfully created")
    })
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamCreateDto newTeam) {
        return new ResponseEntity<>(teamService.createTeam(newTeam), HttpStatus.CREATED);
    }


    @Operation(description = "Updates a team based on TeamCreateDto")
    @PutMapping("/{id}/update")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Employee/ team not found"),
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted")
    })
    public ResponseEntity<TeamDto> updateTeam(@PathVariable Long id,
                                              @RequestBody TeamCreateDto updateTeamDto) {
        return new ResponseEntity<>(teamService.updateTeam(id, updateTeamDto), HttpStatus.OK);
    }

    @Operation(description = "Deletes a team based on team id")
    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Team not found"),
            @ApiResponse(responseCode = "200", description = "Team deleted")
    })
    public ResponseEntity<TeamDto> deleteTeam(@PathVariable Long id) {
        return new ResponseEntity<>(teamService.deleteTeam(id), HttpStatus.OK);
    }
}
