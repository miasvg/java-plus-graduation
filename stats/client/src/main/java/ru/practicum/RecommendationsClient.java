package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.grpc.stats.event.RecommendationsControllerGrpc;
import stats.messages.analyzer.AnalyzerMessages;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Service
@Slf4j
public class RecommendationsClient {

    private final RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public RecommendationsClient(@GrpcClient("ANALYZER") RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client) {
        this.client = client;
        log.info("RecommendationsClient initialized with gRPC stub for service: ANALYZER");
    }

    public Stream<AnalyzerMessages.RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        log.debug("Getting recommendations for user: {}, maxResults: {}", userId, maxResults);

        try {
            AnalyzerMessages.UserPredictionsRequestProto request = AnalyzerMessages.UserPredictionsRequestProto.newBuilder()
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            log.debug("Sending gRPC request: {}", request);
            Iterator<AnalyzerMessages.RecommendedEventProto> iterator = client.getRecommendationsForUser(request);

            List<AnalyzerMessages.RecommendedEventProto> results = new ArrayList<>();
            int count = 0;
            while (iterator.hasNext()) {
                AnalyzerMessages.RecommendedEventProto item = iterator.next();
                results.add(item);
                count++;
                log.trace("Received recommendation {}: {}", count, item);
            }

            log.debug("Received {} recommendations for user: {}", count, userId);
            return results.stream();

        } catch (Exception e) {
            log.error("Error getting recommendations for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to get recommendations for user: " + userId, e);
        }
    }

    public Stream<AnalyzerMessages.RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        log.debug("Getting similar events for event: {}, user: {}, maxResults: {}", eventId, userId, maxResults);

        try {
            AnalyzerMessages.SimilarEventsRequestProto request = AnalyzerMessages.SimilarEventsRequestProto.newBuilder()
                    .setEventId(eventId)
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            log.debug("Sending similar events request: {}", request);
            Iterator<AnalyzerMessages.RecommendedEventProto> iterator = client.getSimilarEvents(request);

            List<AnalyzerMessages.RecommendedEventProto> results = new ArrayList<>();
            int count = 0;
            while (iterator.hasNext()) {
                AnalyzerMessages.RecommendedEventProto item = iterator.next();
                results.add(item);
                count++;
                log.trace("Received similar event {}: {}", count, item);
            }

            log.debug("Received {} similar events for event: {}", count, eventId);
            return results.stream();

        } catch (Exception e) {
            log.error("Error getting similar events for event {}: {}", eventId, e.getMessage(), e);
            throw new RuntimeException("Failed to get similar events for event: " + eventId, e);
        }
    }

    public Stream<AnalyzerMessages.RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        log.debug("Getting interactions count for events: {}", eventIds);

        try {
            AnalyzerMessages.InteractionsCountRequestProto request = AnalyzerMessages.InteractionsCountRequestProto.newBuilder()
                    .addAllEventId(eventIds)
                    .build();

            log.debug("Sending interactions count request for {} events", eventIds.size());
            Iterator<AnalyzerMessages.RecommendedEventProto> iterator = client.getInteractionsCount(request);

            List<AnalyzerMessages.RecommendedEventProto> results = new ArrayList<>();
            int count = 0;
            while (iterator.hasNext()) {
                AnalyzerMessages.RecommendedEventProto item = iterator.next();
                results.add(item);
                count++;
                log.trace("Received interaction count {}: {}", count, item);
            }

            log.debug("Received interaction counts for {} events", count);
            return results.stream();

        } catch (Exception e) {
            log.error("Error getting interactions count for events {}: {}", eventIds, e.getMessage(), e);
            throw new RuntimeException("Failed to get interactions count for events", e);
        }
    }


    public boolean isServiceAvailable() {
        try {
            log.debug("Checking gRPC service availability");
            client.getInteractionsCount(
                    AnalyzerMessages.InteractionsCountRequestProto.newBuilder()
                            .addEventId(0L) // тестовый ID
                            .build()
            ).hasNext();
            log.debug("gRPC service is available");
            return true;
        } catch (Exception e) {
            log.warn("gRPC service is not available: {}", e.getMessage());
            return false;
        }
    }

    private Stream<AnalyzerMessages.RecommendedEventProto> asStream(Iterator<AnalyzerMessages.RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
