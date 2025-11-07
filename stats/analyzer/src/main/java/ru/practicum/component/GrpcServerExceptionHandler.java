package ru.practicum.component;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GrpcServerExceptionHandler {

    @Bean
    public GrpcServerConfigurer exceptionHandlerConfigurer() {
        return serverBuilder -> {
            serverBuilder.intercept(new ServerInterceptor() {
                @Override
                public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                        ServerCall<ReqT, RespT> call,
                        Metadata headers,
                        ServerCallHandler<ReqT, RespT> next) {

                    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(
                            new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                                @Override
                                public void close(Status status, Metadata trailers) {
                                    if (status.getCode() == Status.Code.UNKNOWN &&
                                            status.getDescription() == null) {
                                        log.error("Unknown error in gRPC call: {}", call.getMethodDescriptor().getFullMethodName());
                                        super.close(Status.INTERNAL.withDescription("Internal server error"), trailers);
                                    } else {
                                        super.close(status, trailers);
                                    }
                                }
                            }, headers)) {
                    };
                }
            });
        };
    }
}
