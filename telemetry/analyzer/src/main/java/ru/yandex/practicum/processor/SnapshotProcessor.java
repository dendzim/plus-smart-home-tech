package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.VoidDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserilizer.SensorsSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AnalyzerService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class SnapshotProcessor {

    @Value("${analyzer.kafka.topic.snapshots}")
    private String snapshotsTopic;

    @Value("${analyzer.kafka.poll.timeout}")
    private long pollTimeoutMs;
    private Duration CONSUME_ATTEMPT_TIMEOUT;

    private final AnalyzerService analyzerService;
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    @Autowired
    public SnapshotProcessor(AnalyzerService analyzerService,
                             @Value("${analyzer.kafka.bootstrap}") String bootstrapServer,
                             @Value("${analyzer.kafka.poll.timeout}") long pollTimeoutMs) {
        this.analyzerService = analyzerService;
        this.consumer = new KafkaConsumer<>(getConsumerConfig(bootstrapServer));
        this.CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(pollTimeoutMs);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    private Properties getConsumerConfig(String bootstrapServer) {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer.snapshot.id");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, VoidDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return config;
    }

    public void start() {

        try {
            consumer.subscribe(List.of(snapshotsTopic));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> consumerRecords = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (consumerRecords.isEmpty()) {
                    continue;
                }

                Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();

                for (TopicPartition partition : consumerRecords.partitions()) {
                    List<ConsumerRecord<String, SensorsSnapshotAvro>> partitionRecords = consumerRecords.records(partition);

                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : partitionRecords) {
                        analyzerService.handleSnapshot(record.value());
                    }

                    long nextOffset = partitionRecords.getLast().offset() + 1;
                    offsetsToCommit.put(partition, new OffsetAndMetadata(nextOffset));
                }

                consumer.commitAsync(offsetsToCommit, (offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Ошибка фиксации оффсетов: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка обработки снапшотов", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}

