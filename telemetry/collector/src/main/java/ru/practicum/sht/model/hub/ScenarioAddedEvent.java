package ru.practicum.sht.model.hub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    private String name;

    @Valid
    @NotEmpty
    private List<ScenarioCondition> conditions;

    @Valid
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
