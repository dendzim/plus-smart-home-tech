package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sensors")
public class Sensor {
    @Id
    private String id;

    private String hubId;

    @OneToMany(mappedBy = "sensor")
    private Set<ScenarioCondition> conditions;

    @OneToMany(mappedBy = "sensor")
    private Set<ScenarioAction> actions;
}
