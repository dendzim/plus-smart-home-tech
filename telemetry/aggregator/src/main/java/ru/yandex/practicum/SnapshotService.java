package ru.yandex.practicum;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        final SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(
                event.getHubId(),
                k -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(event.getTimestamp())
                        .setSensorsState(new ConcurrentHashMap<>())
                        .build()
        );

        SensorStateAvro sensorStateAvro = snapshot.getSensorsState().get(event.getId());

        if (sensorStateAvro != null && (Objects.equals(sensorStateAvro.getData(), event.getPayload()) ||
                sensorStateAvro.getTimestamp().isAfter(event.getTimestamp()))) {
            return Optional.empty();
        }

        SensorStateAvro stateAvro = SensorStateAvro.newBuilder()
                .setData(event.getPayload())
                .setTimestamp(event.getTimestamp())
                .build();

        snapshot.setTimestamp(event.getTimestamp());
        snapshot.getSensorsState().put(event.getId(), stateAvro);

        return Optional.of(snapshot);
    }
}
