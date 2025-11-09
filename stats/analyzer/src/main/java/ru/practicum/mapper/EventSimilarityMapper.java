package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

public class EventSimilarityMapper {

    public static EventSimilarity mapToEventSimilarity(EventSimilarityAvro avro) {
        return EventSimilarity.builder()
                .eventA(avro.getEventA())
                .eventB(avro.getEventB())
                .score(avro.getScore())
                .timestamp(avro.getTimestamp())
                .build();
    }
}
