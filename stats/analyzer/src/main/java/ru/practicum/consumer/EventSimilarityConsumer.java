package ru.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.handlers.EventSimilarityHandler;


import java.time.Duration;
import java.util.List;

@Slf4j
@Component

public class EventSimilarityConsumer implements Runnable {


    private final Consumer<String, EventSimilarityAvro> consumer;
    private final EventSimilarityHandler eventSimilarityHandler;

    @Autowired
    public EventSimilarityConsumer(@Qualifier("eventKafkaConsumer") Consumer<String, EventSimilarityAvro> consumer, EventSimilarityHandler eventSimilarityHandler) {
        this.consumer = consumer;
        this.eventSimilarityHandler = eventSimilarityHandler;
    }

    @Value("${analyzer.kafka.consumer.events.topic}")
    private String topicEventSimilarity;

    @Value("${analyzer.kafka.consumer.poll-timeout-ms}")
    private int pollTimeout;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(topicEventSimilarity));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records =
                        consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    EventSimilarityAvro eventSimilarity = record.value();
                    log.info("Получили коэффициент схожести: {}", eventSimilarity);
                    eventSimilarityHandler.handle(eventSimilarity);
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            log.info("Получен сигнал завершения для events-similarity consumer");
        } catch (Exception e) {
            log.error("Ошибка при чтении данных из топика {}", topicEventSimilarity, e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем consumer events-similarity");
                consumer.close();
            }
        }
    }
}
