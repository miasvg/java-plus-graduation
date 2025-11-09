package ru.practicum;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.AggregatorService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final String USER_ACTION_TOPIC = "stats.user-actions.v1";
    private static final String SIMILARITY_TOPIC = "stats.events-similarity.v1";

    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final AggregatorService aggregatorService;

    @PostConstruct
    public void start() {
        consumer.subscribe(List.of(USER_ACTION_TOPIC));

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(1));
                    if (!records.isEmpty()) {
                        for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                            try {
                                List<EventSimilarityAvro> list = aggregatorService
                                        .aggregate(record.value());

                                if (list.isEmpty()) {
                                    continue;
                                }

                                list.forEach(x -> producer.send(new ProducerRecord<>(
                                        SIMILARITY_TOPIC,
                                        null,
                                        x.getEventA() + "_" + x.getEventB(),
                                        x)));
                            } catch (Exception e) {
                                log.error("Ошибка обработки сообщения с ключом: {}", record.key(), e);

                            }
                        }
                        consumer.commitSync();
                    }
                }
            } catch (WakeupException ignored) {
                log.info("Получен сигнал остановки консьюмера");
            } catch (Exception e) {
                log.error("Ошибка во время обработки событий от датчиков", e);
            } finally {
                try {
                    log.info("Флашим продюсер перед закрытием");
                    producer.flush();
                } finally {
                    log.info("Закрываем консьюмер");
                    consumer.close(Duration.ofSeconds(10));
                    log.info("Закрываем продюсер");
                    producer.close(Duration.ofSeconds(10));
                }
            }
        }, "aggregation-thread");

        thread.start();
    }
}
