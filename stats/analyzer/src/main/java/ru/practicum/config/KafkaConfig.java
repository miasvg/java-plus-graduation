package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
@Getter
@Setter
public class KafkaConfig {

    @Value("${analyzer.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${analyzer.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${analyzer.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${analyzer.kafka.consumer.poll-timeout-ms}")
    private int pollTimeoutMs;


    @Value("${analyzer.kafka.consumer.actions.group-id}")
    private String actionsGroupId;

    @Value("${analyzer.kafka.consumer.actions.value-deserializer}")
    private String actionsValueDeserializer;


    @Value("${analyzer.kafka.consumer.events.group-id}")
    private String eventsGroupId;

    @Value("${analyzer.kafka.consumer.events.value-deserializer}")
    private String eventsValueDeserializer;


    @Bean
    public Properties actionConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, actionsValueDeserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, actionsGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        return props;
    }

    @Bean
    public Properties eventConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, eventsValueDeserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, eventsGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        return props;
    }


    @Bean(name = "userActionKafkaConsumer")
    public Consumer<String, UserActionAvro> userActionConsumer() {
        return new KafkaConsumer<>(actionConsumerProps());
    }

    @Bean(name = "eventKafkaConsumer")
    public Consumer<String, EventSimilarityAvro> eventSimilarityConsumer() {
        return new KafkaConsumer<>(eventConsumerProps());
    }
}
