package com.example.employeeApplication.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team")
public class TeamEntity {
    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "team_lead_id", referencedColumnName = "employee_id")
    private EmployeeEntity teamLead;

    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    Set<EmployeeEntity> employeesInTeam;

}
