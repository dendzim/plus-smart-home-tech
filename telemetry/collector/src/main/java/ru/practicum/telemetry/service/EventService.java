package ru.practicum.telemetry.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.telemetry.kafka.KafkaProducer;
import ru.practicum.telemetry.mapper.HubMapper;
import ru.practicum.telemetry.mapper.SensorMapper;
import ru.practicum.telemetry.model.hub.HubEvent;
import ru.practicum.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    @Value("${collector.kafka.topic.sensors}")
    private String sensorsTopic;
    @Value("${collector.kafka.topic.hubs}")
    private String hubsTopic;

    private final HubMapper hubMapper;
    private final SensorMapper sensorMapper;
    private final KafkaProducer producer;

    public void addSensorEvent(@Valid SensorEvent sensorEvent) {
        SensorEventAvro sensorEventAvro = sensorMapper.toAvro(sensorEvent);
        Long timestamp = sensorEvent.getTimestamp().toEpochMilli();
        String key = sensorEvent.getHubId();

        CompletableFuture<RecordMetadata> future = CompletableFuture.supplyAsync(() -> {
            try {
                return producer.getProducer()
                        .send(new ProducerRecord<>(sensorsTopic, null, timestamp, key, sensorEventAvro))
                        .get();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при отправке события от датчика", e);
            }
        });

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка отправки события от датчика: ключ={}, ошибка={}",
                        key, ex.getMessage(), ex);
            } else {
                log.info("Событие от датчика отправлено: топик={}, партиция={}, оффсет={}, ключ={}",
                        result.topic(), result.partition(), result.offset(), key);
            }
        });
    }

    public void addHubEvent(@Valid HubEvent hubEvent) {
        HubEventAvro hubEventAvro = hubMapper.toAvro(hubEvent);
        Long timestamp = hubEvent.getTimestamp().toEpochMilli();
        String key = hubEvent.getHubId();

        CompletableFuture<RecordMetadata> future = CompletableFuture.supplyAsync(() -> {
            try {
                return producer.getProducer()
                        .send(new ProducerRecord<>(hubsTopic, null, timestamp, key, hubEventAvro))
                        .get();
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при отправке события от хаба", e);
            }
        });

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка отправки события от хаба: ключ={}, ошибка={}",
                        key, ex.getMessage(), ex);
            } else {
                log.info("Событие от хаба отправлено: топик={}, партиция={}, оффсет={}, ключ={}",
                        result.topic(), result.partition(), result.offset(), key);
            }
        });
    }
}
