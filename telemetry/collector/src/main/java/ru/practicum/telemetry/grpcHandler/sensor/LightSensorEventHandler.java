package ru.practicum.telemetry.grpcHandler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.kafka.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    @Value("${collector.kafka.topic.sensors}")
    private String sensorsTopic;

    protected final KafkaProducer client;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        LightSensorProto protoEvent = event.getLightSensor();

        LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(protoEvent.getLinkQuality())
                .setLuminosity(protoEvent.getLuminosity())
                .build();

        Instant instant = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());

        Long timestamp = instant.toEpochMilli();
        String key = event.getHubId();

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(instant)
                .setPayload(lightSensorAvro)
                .build();

        CompletableFuture<RecordMetadata> future = CompletableFuture.supplyAsync(() -> {
            try {
                return client.getProducer()
                        .send(new ProducerRecord<>(sensorsTopic, null, timestamp, key, eventAvro))
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
