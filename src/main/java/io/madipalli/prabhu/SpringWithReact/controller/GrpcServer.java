package io.madipalli.prabhu.SpringWithReact.controller;

import io.grpc.stub.StreamObserver;
import io.madipalli.prabhu.grpc.HelloReply;
import io.madipalli.prabhu.grpc.HelloRequest;
import io.madipalli.prabhu.grpc.SpringWithReactGrpc;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GrpcServer extends SpringWithReactGrpc.SpringWithReactImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("In GrpcServer sayHello");
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello World! %s".formatted(request.getName())).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> streamHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(final HelloRequest request) {
                responseObserver.onNext(
                        HelloReply.newBuilder()
                                .setMessage("Hello World! %s".formatted(request.getName()))
                                .build()
                );
            }

            @Override
            public void onError(final Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
                responseObserver.onNext(HelloReply.newBuilder()
                        .setMessage("Received an exception %s".formatted(throwable.getMessage()))
                        .build());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(HelloReply.newBuilder()
                                .setMessage("About to close")
                        .build());
                responseObserver.onCompleted();
            }
        };
    }
}
