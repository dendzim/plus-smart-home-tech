package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserilizer.HubEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.AnalyzerService;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    @Value("${analyzer.kafka.topic.hubs}")
    private String hubsTopic;

    @Value("${analyzer.kafka.poll.timeout}")
    private long pollTimeoutMs;
    private Duration CONSUME_ATTEMPT_TIMEOUT;
    private final AnalyzerService analyzerService;
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Autowired
    public HubEventProcessor(AnalyzerService analyzerService,
                             @Value("${analyzer.kafka.bootstrap}") String bootstrapServer,
                             @Value("${analyzer.kafka.poll.timeout}") long pollTimeoutMs) {
        this.analyzerService = analyzerService;
        this.consumer = new KafkaConsumer<>(getConsumerConfig(bootstrapServer));
        this.CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(pollTimeoutMs);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
    }

    private Properties getConsumerConfig(String bootstrapServer) {
        Properties config = new Properties();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer.hub.id");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return config;
    }

    @Override
    public void run() {

        try {
            consumer.subscribe(List.of(hubsTopic));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (records.isEmpty()) {
                    continue;
                }

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        analyzerService.handleHub(record.value());
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1));
                    } catch (Exception e) {
                        log.error("Ошибка обработки события хаба с оффсетом {}", record.offset(), e);
                    }
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка обработки событий хаба", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Консьюмер закрыт");
                consumer.close();
            }
        }
    }
}
