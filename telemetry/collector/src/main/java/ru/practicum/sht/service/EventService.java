package ru.practicum.sht.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.sht.kafka.KafkaAvroProducer;
import ru.practicum.sht.mapper.HubMapper;
import ru.practicum.sht.mapper.SensorMapper;
import ru.practicum.sht.model.hub.HubEvent;
import ru.practicum.sht.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class EventService {

    @Value("${sht.telemetry.sensors.topic}")
    private String sensorsTopic;
    @Value("${sht.telemetry.hubs.topic}")
    private String hubsTopic;

    private final HubMapper hubMapper;
    private final SensorMapper sensorMapper;
    private final KafkaAvroProducer producer;

    public void addSensorEvent(@Valid SensorEvent sensorEvent) {
        SensorEventAvro sensorEventAvro = sensorMapper.toAvro(sensorEvent);
        producer.getProducer().send(new ProducerRecord<>(sensorsTopic, sensorEventAvro));
    }

    public void addHubEvent(@Valid HubEvent hubEvent) {
        HubEventAvro hubEventAvro = hubMapper.toAvro(hubEvent);
        producer.getProducer().send(new ProducerRecord<>(hubsTopic, hubEventAvro));
    }
}
