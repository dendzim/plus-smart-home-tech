package ru.practicum.telemetry.grpcHandler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.kafka.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {
    @Value("${collector.kafka.topic.hubs}")
    private String hubsTopic;

    protected final KafkaProducer client;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        Instant instant = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());

        ScenarioRemovedEventProto deviceEvent = event.getScenarioRemoved();

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(deviceEvent.getName())
                .build();

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(scenarioRemovedEventAvro)
                .build();

        String key = event.getHubId();
        long kafkaTimestamp = instant.toEpochMilli();

        CompletableFuture<RecordMetadata> future = CompletableFuture.supplyAsync(() -> {
            try {
                return client.getProducer()
                        .send(new ProducerRecord<>(hubsTopic, null, kafkaTimestamp, key, eventAvro))
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
