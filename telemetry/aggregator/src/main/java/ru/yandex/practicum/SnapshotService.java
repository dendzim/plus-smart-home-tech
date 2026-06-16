package ru.yandex.practicum;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        final SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.computeIfAbsent(
                event.getHubId(),
                key -> SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(event.getTimestamp())
                        .build()
        );

        SensorStateAvro oldState = sensorsSnapshotAvro.getSensorsState().get(event.getId());

        if (oldState != null &&
                (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                        Objects.equals(oldState.getData(), event.getPayload()))) {
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
