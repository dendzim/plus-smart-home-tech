package ru.yandex.practicum.service;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Transactional
    public void handleHub(HubEventAvro hubEventAvro) {
        String hubId = hubEventAvro.getHubId();
        Object payload = hubEventAvro.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAddedEventAvro) {
            addDevice(hubId, deviceAddedEventAvro);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemovedEventAvro) {
            removeDevice(hubId, deviceRemovedEventAvro);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAddedEventAvro) {
            saveScenario(hubId, scenarioAddedEventAvro);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemovedEventAvro) {
            removeScenario(hubId, scenarioRemovedEventAvro);
        }
    }

    private void removeScenario(String hubId, ScenarioRemovedEventAvro scenarioRemovedEventAvro) {
        scenarioRepository.findByHubIdAndName(hubId, scenarioRemovedEventAvro.getName())
                .ifPresent(scenarioRepository::delete);
    }

    private void saveScenario(String hubId, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        String name = scenarioAddedEventAvro.getName();
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> Scenario.builder().hubId(hubId).name(name).build());

        if (scenario.getConditions() == null) {
            scenario.setConditions(new HashMap<>());
        }
        if (scenario.getActions() == null) {
            scenario.setActions(new HashMap<>());
        }

        scenario.setHubId(hubId);
        scenario.setName(name);
        scenario.getConditions().clear();
        scenario.getActions().clear();

        Map<String, Sensor> sensors = new HashMap<>();

        scenarioAddedEventAvro.getConditions().forEach(conditionAvro -> {
            String sensorId = conditionAvro.getSensorId();
            Sensor sensor = sensors.computeIfAbsent(sensorId, id -> ensureSensorExists(id, hubId));
            Condition condition = Condition.builder()
                    .type(ConditionType.valueOf(conditionAvro.getType().name()))
                    .operation(ConditionOperation.valueOf(conditionAvro.getOperation().name()))
                    .value(asInteger(conditionAvro.getValue()))
                    .build();
            scenario.getConditions().put(sensorId, condition);
        });

        scenarioAddedEventAvro.getActions().forEach(actionAvro -> {
            String sensorId = actionAvro.getSensorId();
            Sensor sensor = sensors.computeIfAbsent(sensorId, id -> ensureSensorExists(id, hubId));
            Action action = Action.builder()
                    .type(ActionType.valueOf(actionAvro.getType().name()))
                    .value(asInteger(actionAvro.getValue()))
                    .build();
            scenario.getActions().put(sensorId, action);  // ← исправлено: добавлен sensorId
        });

        scenarioRepository.save(scenario);
        log.info("Сценарий сохранен: hubId={}, name={}, conditions={}, actions={}",
                hubId, name, scenario.getConditions().size(), scenario.getActions().size());
    }

    private Sensor ensureSensorExists(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseGet(() -> sensorRepository.save(Sensor.builder().id(sensorId).hubId(hubId).build()));
    }

    private void addDevice(String hubId, DeviceAddedEventAvro deviceAddedEventAvro) {
        String sensorId = deviceAddedEventAvro.getId();

        if (sensorRepository.findByIdAndHubId(sensorId, hubId).isEmpty()) {
            Sensor sensor = Sensor.builder()
                    .id(sensorId)
                    .hubId(hubId)
                    .build();
            sensorRepository.save(sensor);
            log.debug("Устройство добавлено: hubId={}, sensorId={}", hubId, sensorId);
        } else {
            log.debug("Устройство уже есть: hubId={}, sensorId={}", hubId, sensorId);
        }
    }

    private void removeDevice(String hubId, DeviceRemovedEventAvro deviceRemovedEventAvro) {
        String sensorId = deviceRemovedEventAvro.getId();
        sensorRepository.findByIdAndHubId(sensorId, hubId).ifPresent(sensor -> {
            List<Scenario> scenarios = scenarioRepository.findWithSensorsByHubId(hubId);

            for (Scenario scenario : scenarios) {
                scenario.getConditions().remove(sensorId);
                scenario.getActions().remove(sensorId);
            }
            sensorRepository.delete(sensor);
        });
    }

    @Transactional
    public void handleSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        String hubId = sensorsSnapshotAvro.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            return;
        }

        for (Scenario scenario : scenarios) {
            if (isScenarioIsDone(scenario, sensorsSnapshotAvro)) {
                sendActions(scenario, sensorsSnapshotAvro);
            }
        }
    }

    private boolean isScenarioIsDone(Scenario scenario, SensorsSnapshotAvro snapshotAvro) {
        Map<String, SensorStateAvro> sensorsState = snapshotAvro.getSensorsState();
        Map<String, Condition> conditions = scenario.getConditions();

        if (conditions.isEmpty()) {
            log.warn("У сценария  {} нет условий", scenario.getId());
            return false;
        }

        return conditions.entrySet().stream().allMatch(entry -> {
            String sensorId = entry.getKey();
            Condition condition = entry.getValue();

            SensorStateAvro sensorState = sensorsState.get(sensorId);
            if (sensorState == null) {
                log.debug("Сеснсор {} не найден в снапшоте", sensorId);
                return false;
            }

            Optional<Integer> currentValue = readValue(condition.getType(), sensorState.getData());
            return currentValue.map(value -> compareValues(value, condition)).orElse(false);
        });
    }

    private Optional<Integer> readValue(ConditionType type, Object payload) {
        if (type == null || payload == null) return Optional.empty();

        return switch (type) {
            case MOTION -> payload instanceof MotionSensorAvro m
                    ? Optional.of(Boolean.compare(m.getMotion(), false))
                    : Optional.empty();
            case SWITCH -> payload instanceof SwitchSensorAvro s
                    ? Optional.of(Boolean.compare(s.getState(), false))
                    : Optional.empty();
            case CO2LEVEL -> payload instanceof ClimateSensorAvro c
                    ? Optional.of(c.getCo2Level()) : Optional.empty();
            case HUMIDITY -> payload instanceof ClimateSensorAvro c
                    ? Optional.of(c.getHumidity()) : Optional.empty();
            case LUMINOSITY -> payload instanceof LightSensorAvro l
                    ? Optional.of(l.getLuminosity()) : Optional.empty();
            case TEMPERATURE -> extractTemperature(payload);
        };
    }

    private Optional<Integer> extractTemperature(Object payload) {
        if (payload instanceof TemperatureSensorAvro t) {
            return Optional.of(t.getTemperatureC());
        }
        if (payload instanceof ClimateSensorAvro c) {
            return Optional.of(c.getTemperatureC());
        }
        return Optional.empty();
    }

    private Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        log.warn("Тип данных не распознан: {}", value.getClass().getSimpleName());
        return null;
    }

    private boolean compareValues(Integer currentValue, Condition condition) {
        Integer expected = condition.getValue();
        if (expected == null) {
            return false;
        }

        return switch (condition.getOperation()) {
            case EQUALS -> Objects.equals(currentValue, expected);
            case GREATER_THAN -> currentValue > expected;
            case LOWER_THAN -> currentValue < expected;
        };
    }

    private void sendActions(Scenario scenario, SensorsSnapshotAvro snapshotAvro) {
        String hubId = snapshotAvro.getHubId();
        String scenarioName = scenario.getName();
        long timestampMillis = snapshotAvro.getTimestamp().toEpochMilli();

        scenario.getActions().forEach((sensorId, action) -> {
            try {
                DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                        .setSensorId(sensorId)
                        .setType(ActionTypeProto.valueOf(action.getType().name()));

                if (action.getValue() != null) {
                    actionBuilder.setValue(action.getValue());
                }

                hubRouterClient.handleDeviceAction(DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(actionBuilder.build())
                        .setTimestamp(Timestamps.fromMillis(timestampMillis))
                        .build());

                log.debug("Действие отправлено: sensorId={}, type={}", sensorId, action.getType());
            } catch (Exception e) {
                log.error("Ошибка при отправки действия сенсору {}: {}", sensorId, e.getMessage(), e);
            }
        });
    }
}