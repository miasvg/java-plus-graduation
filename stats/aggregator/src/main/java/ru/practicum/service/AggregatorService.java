package ru.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AggregatorService {
    private final Double VIEW_WEIGHT = 0.4;
    private final Double REGISTER_WEIGHT = 0.8;
    private final Double LIKE_WEIGHT = 1.0;
    private Map<Long, Map<Long, Double>> actionWeightMap = new HashMap<>();
    private Map<Long, Double> ownWeightSum = new HashMap<>();
    private Map<Long, Map<Long, Double>> minWeightSums = new HashMap<>();


    public List<EventSimilarityAvro> aggregate(SpecificRecordBase recordBase) {
        if (!(recordBase instanceof UserActionAvro action)) {
            throw new IllegalArgumentException(
                    "Unsupported record type: " + recordBase.getClass().getName()
            );
        }
        List<EventSimilarityAvro> answer = new ArrayList<>();
        Map<Long, Double> usersWeight = actionWeightMap.computeIfAbsent(
                action.getEventId(),
                k -> new HashMap<>()
        );
        double actionWeight = 0.0;
        switch (action.getActionType()) {
            case VIEW -> actionWeight = VIEW_WEIGHT;
            case REGISTER -> actionWeight = REGISTER_WEIGHT;
            case LIKE -> actionWeight = LIKE_WEIGHT;
        }
        Double currentWeight = usersWeight.getOrDefault(action.getUserId(), 0.0);

        if (actionWeight <= currentWeight) {
            return answer;
        }
        usersWeight.put(action.getUserId(), actionWeight);
        ownWeightSum.put(action.getEventId(), ownWeightSum.getOrDefault(action.getEventId(), 0.0) +
                actionWeight - currentWeight);

        for (Long key : actionWeightMap.keySet()) {
            if (key == action.getEventId()) {
                continue;
            }
            if (actionWeightMap.get(key).containsKey(action.getUserId())) {


                double minWeightSum = get(action.getEventId(), key);
                double event2Weight = actionWeightMap.get(key).get(action.getUserId());
                minWeightSum += Math.min(event2Weight, actionWeight) - Math.min(event2Weight, currentWeight);
                double s1 = ownWeightSum.get(action.getEventId());
                double s2 = ownWeightSum.get(key);
                long eventA = Math.min(action.getEventId(), key);
                long eventB = Math.max(action.getEventId(), key);
                double sin = sin(s1, s2, minWeightSum);
                EventSimilarityAvro eventSimilarityAvro = new EventSimilarityAvro();


                eventSimilarityAvro.setEventA(eventA);
                eventSimilarityAvro.setEventB(eventB);

                eventSimilarityAvro.setScore(sin);
                eventSimilarityAvro.setTimestamp(Instant.now());
                answer.add(eventSimilarityAvro);
                put(action.getEventId(), key, minWeightSum);
            }
        }

        return answer;

    }

    private double sin(double s1, double s2, double minWeightSum) {
        if (s1 == 0.0 || s2 == 0.0) {
            return 0.0;
        }
        return minWeightSum / (Math.sqrt(s1) * Math.sqrt(s2));
    }

    private void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    private double get(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }
}