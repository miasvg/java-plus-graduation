package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.producer.KafkaProducerConfig;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorService {

    private final KafkaProducerConfig.EventProducer eventProducer;

    public void sendToKafka(String topic, SpecificRecordBase message) {
        log.info("Отправка сообщения в топик {}", topic);
        eventProducer.getProducer().send(new ProducerRecord<>(topic, message), (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке в Kafka: {}", exception.getMessage(), exception);
            } else {
                log.debug("Сообщение записано в {}: offset={}, partition={}",
                        metadata.topic(), metadata.offset(), metadata.partition());
            }
        });
    }
}
