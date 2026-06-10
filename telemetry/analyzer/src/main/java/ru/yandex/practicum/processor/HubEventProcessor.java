package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    @Value("${sht.telemetry.hubs.topic}")
    private String hubsTopic;
    private final Duration consumeAttemptTimeout;

    public HubEventProcessor(Duration consumeAttemptTimeout) {
        this.consumeAttemptTimeout = consumeAttemptTimeout;
    }

    @Override
    public void run() {

    }
}
