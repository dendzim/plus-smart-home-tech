package ru.practicum.telemetry.mapper;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubMapper {
    public HubEventAvro toAvro(@Valid HubEvent hubEvent) {
        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp());
        switch (hubEvent) {
            case DeviceAddedEvent e -> {
                return builder.setPayload(toDeviceAddedAvro(e)).build();
            }
            case DeviceRemovedEvent e -> {
                return builder.setPayload(toDeviceRemovedAvro(e)).build();
            }
            case ScenarioAddedEvent e -> {
                return builder.setPayload(toScenarioAddedAvro(e)).build();
            }
            case ScenarioRemovedEvent e -> {
                return builder.setPayload(toScenarioRemovedAvro(e)).build();
            }
            default -> throw new IllegalArgumentException("Unknown type: " + hubEvent.getClass());
        }
    }

    private DeviceAddedEventAvro toDeviceAddedAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(toDeviceTypeAvro(event.getDeviceType()))
                .build();
    }

    private DeviceRemovedEventAvro toDeviceRemovedAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    private ScenarioAddedEventAvro toScenarioAddedAvro(ScenarioAddedEvent event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(toScenarioConditionAvroList(event.getConditions()))
                .setActions(toDeviceActionAvroList(event.getActions()))
                .build();
    }

    private ScenarioRemovedEventAvro toScenarioRemovedAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    private DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return switch (deviceType) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
        };
    }

    private List<ScenarioConditionAvro> toScenarioConditionAvroList(List<ScenarioCondition> conditions) {
        if (conditions == null) {
            return null;
        }

        return conditions.stream()
                .map(this::toScenarioConditionAvro)
                .collect(Collectors.toList());
    }

    private ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition condition) {
        if (condition == null) {
            return null;
        }
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(toConditionTypeAvro(condition.getType()))
                .setOperation(toConditionOperationAvro(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    private ConditionTypeAvro toConditionTypeAvro(ConditionType conditionType) {
        return switch (conditionType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
    }

    private ConditionOperationAvro toConditionOperationAvro(ConditionOperation conditionOperation) {
        return switch (conditionOperation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
        };
    }

    private ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            case INVERSE -> ActionTypeAvro.INVERSE;
        };
    }

    private List<DeviceActionAvro> toDeviceActionAvroList(List<DeviceAction> deviceActions) {
        if (deviceActions == null || deviceActions.isEmpty()) {
            return List.of();
        }
        return deviceActions.stream()
                .map(this::toDeviceActionAvro)
                .collect(Collectors.toList());
    }

    private DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        if (deviceAction == null) {
            return null;
        }
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }
}
