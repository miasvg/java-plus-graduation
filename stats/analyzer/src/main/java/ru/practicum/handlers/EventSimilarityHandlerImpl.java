package ru.practicum.handlers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.EventSimilarityMapper;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityHandlerImpl implements EventSimilarityHandler {

    private final EventSimilarityRepository repository;

    @Transactional
    @Override
    public void handle(EventSimilarityAvro avro) {
        Long eventA = avro.getEventA();
        Long eventB = avro.getEventB();

        if (!repository.existsByEventAAndEventB(eventA, eventB)) {
            repository.save(EventSimilarityMapper.mapToEventSimilarity(avro));
            log.debug("Сохранена новая схожесть событий {}", avro);
        } else {
            EventSimilarity existing = repository.findByEventAAndEventB(eventA, eventB);
            existing.setScore(avro.getScore());
            existing.setTimestamp(avro.getTimestamp());
            log.debug("Обновлена схожесть событий {}", existing);
        }
    }
}
