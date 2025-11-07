package ru.practicum.controllers;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.service.CollectorService;
import ru.yandex.practicum.grpc.stats.action.UserActionControllerGrpc;
import stats.messages.collector.UserAction;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionMapper userActionMapper;
    private final CollectorService collectorService;

    @Override
    public void collectUserAction(UserAction.UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Выполняю действие с User Actions {}", request);
        try {
            collectorService.sendToKafka("stats.user-actions.v1", userActionMapper.toAvro(request));

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }
}
