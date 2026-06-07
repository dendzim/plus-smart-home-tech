package ru.yandex.practicum;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        final SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.computeIfAbsent(
                event.getHubId(),
                k -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setSensorsState(new ConcurrentHashMap<>())
                        .setTimestamp(event.getTimestamp())
                        .build()
        );

        SensorStateAvro sensorStateAvro = sensorsSnapshotAvro.getSensorsState().get(event.getId());

        if (sensorStateAvro != null && (sensorStateAvro.getTimestamp().isAfter(event.getTimestamp())) ||
                Objects.equals(sensorStateAvro.getData(), event.getPayload()) ) {
            return Optional.empty();
        }

        SensorStateAvro stateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsSnapshotAvro.setTimestamp(event.getTimestamp());
        sensorsSnapshotAvro.getSensorsState().put(event.getId(), stateAvro);

        return Optional.of(sensorsSnapshotAvro);
    }
}
