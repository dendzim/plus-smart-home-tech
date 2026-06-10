package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hubId;
    private String name;

    @OneToMany(mappedBy = "scenario")
    private Set<ScenarioCondition> conditions;

    @OneToMany(mappedBy = "scenario")
    private Set<ScenarioAction> actions;
}
