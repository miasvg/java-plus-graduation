package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import stats.messages.analyzer.AnalyzerMessages;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final EventSimilarityRepository similarityRepository;
    private final UserActionRepository actionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AnalyzerMessages.RecommendedEventProto> getRecommendationsForUser(
            AnalyzerMessages.UserPredictionsRequestProto request
    ) {
        long userId = request.getUserId();
        int maxResults = (int) request.getMaxResults();

        List<Long> userEventIds = actionRepository.findEventIdsOrderByMaxMarkDesc(
                userId, PageRequest.of(0, maxResults)
        );
        if (userEventIds.isEmpty()) {
            return List.of();
        }


        List<EventSimilarity> similarPairs = similarityRepository.findSimilarPairsForEvents(userEventIds);


        Map<Long, Double> userMarks = actionRepository.findAllByUserId(userId).stream()
                .collect(Collectors.toMap(a -> a.getEventId(), a -> a.getMark()));

        // Вычисляем предсказанные оценки
        Map<Long, Double> predictedScores = new HashMap<>();
        Map<Long, Double> similaritySums = new HashMap<>();

        for (EventSimilarity pair : similarPairs) {
            long knownEvent = userEventIds.contains(pair.getEventA()) ? pair.getEventA() : pair.getEventB();
            long candidate = (knownEvent == pair.getEventA()) ? pair.getEventB() : pair.getEventA();
            double sim = pair.getScore();

            Double userMark = userMarks.get(knownEvent);
            if (userMark == null) continue;

            predictedScores.merge(candidate, sim * userMark, Double::sum);
            similaritySums.merge(candidate, sim, Double::sum);
        }
        List<AnalyzerMessages.RecommendedEventProto> results = predictedScores.entrySet().stream()
                .filter(e -> similaritySums.getOrDefault(e.getKey(), 0.0) > 0)
                .map(e -> AnalyzerMessages.RecommendedEventProto.newBuilder()
                        .setEventId(e.getKey())
                        .setScore(e.getValue() / similaritySums.get(e.getKey()))
                        .build())
                .sorted(Comparator.comparingDouble(AnalyzerMessages.RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();

        return results;
    }


    @Override
    @Transactional(readOnly = true)
    public List<AnalyzerMessages.RecommendedEventProto> getSimilarEvents(
            AnalyzerMessages.SimilarEventsRequestProto request
    ) {
        long userId = request.getUserId();
        long eventId = request.getEventId();
        int maxResults = (int) request.getMaxResults();

        Set<Long> interacted = new HashSet<>(actionRepository.findEventIdsByUserId(userId));


        List<EventSimilarity> similarPairs = similarityRepository.findByEventAOrEventB(eventId, eventId);


        return similarPairs.stream()
                .map(pair -> {
                    long candidate = (pair.getEventA() == eventId) ? pair.getEventB() : pair.getEventA();
                    return Map.entry(candidate, pair.getScore());
                })
                .filter(e -> !interacted.contains(e.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(e -> AnalyzerMessages.RecommendedEventProto.newBuilder()
                        .setEventId(e.getKey())
                        .setScore(e.getValue())
                        .build())
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<AnalyzerMessages.RecommendedEventProto> getInteractionsCount(
            AnalyzerMessages.InteractionsCountRequestProto request
    ) {
        List<Long> eventIds = request.getEventIdList();

        List<Object[]> results = actionRepository.sumMarksForEvents(eventIds);

        return results.stream()
                .map(r -> AnalyzerMessages.RecommendedEventProto.newBuilder()
                        .setEventId(((Number) r[0]).longValue())
                        .setScore(((Number) r[1]).doubleValue())
                        .build())
                .toList();
    }
}
