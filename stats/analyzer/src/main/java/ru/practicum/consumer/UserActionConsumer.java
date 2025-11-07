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
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.handlers.UserActionHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component

public class UserActionConsumer implements Runnable {
    private final Consumer<String, UserActionAvro> consumer;
    private final UserActionHandler userActionHandler;

    @Autowired
    public UserActionConsumer(UserActionHandler userActionHandler, @Qualifier("userActionKafkaConsumer") Consumer<String, UserActionAvro> consumer) {
        this.userActionHandler = userActionHandler;
        this.consumer = consumer;
    }

    @Value("${analyzer.kafka.consumer.actions.topic}")
    private String topicUserAction;

    @Value("${analyzer.kafka.consumer.poll-timeout-ms}")
    private int pollTimeout;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(topicUserAction));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, UserActionAvro> records =
                        consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    UserActionAvro action = record.value();
                    log.info("Получили действие пользователя: {}", action);
                    userActionHandler.handle(action);
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            log.info("Получен сигнал завершения для user-action consumer");
        } catch (Exception e) {
            log.error("Ошибка при чтении данных из топика {}", topicUserAction, e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем consumer user-action");
                consumer.close();
            }
        }
    }
}
