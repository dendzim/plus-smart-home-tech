package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class SnapshotProcessor {

    @Value("${sht.telemetry.snapshots.topic}")
    private String snapshotsTopic;
    private final Duration consumeAttemptTimeout;

    public SnapshotProcessor(Duration consumeAttemptTimeout) {
        this.consumeAttemptTimeout = consumeAttemptTimeout;
    }

    public void start() {
    }
}
