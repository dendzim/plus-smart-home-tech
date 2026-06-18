package ru.practicum.sht.grpcHandler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.sht.kafka.KafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    @Value("${collector.kafka.topic.hubs}")
    private String hubsTopic;

    protected final KafkaProducer client;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        if (!event.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: " + event.getPayloadCase());
        }

        Instant instant = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());


        ScenarioAddedEventProto deviceEvent = event.getScenarioAdded();

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(deviceEvent.getName())
                .setConditions(deviceEvent.getConditionList().stream()
                        .map(condition -> ScenarioConditionAvro.newBuilder()
                                .setSensorId(condition.getSensorId())
                                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                                .setValue(condition.getValueCase().equals(ScenarioConditionProto.ValueCase.BOOL_VALUE) ? condition.getBoolValue() : condition.getIntValue())
                                .build())
                        .collect(Collectors.toList()))
                .setActions(deviceEvent.getActionList().stream()
                        .map(action -> DeviceActionAvro.newBuilder()
                                .setSensorId(action.getSensorId())
                                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                                .setValue(action.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(scenarioAddedEventAvro)
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
