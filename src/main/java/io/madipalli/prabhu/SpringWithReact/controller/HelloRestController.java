package io.madipalli.prabhu.SpringWithReact.controller;

import grpcbin.GRPCBinGrpc;
import grpcbin.Grpcbin;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.madipalli.prabhu.grpc.HelloRequest;
import io.madipalli.prabhu.grpc.SpringWithReactGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Log4j2
public class HelloRestController {

    private final SpringWithReactGrpc.SpringWithReactBlockingV2Stub springWithReact =
            SpringWithReactGrpc.newBlockingV2Stub(
                    NettyChannelBuilder.forAddress("localhost", 9090)
                            .usePlaintext()
                            .build()
            );

    private final GRPCBinGrpc.GRPCBinBlockingV2Stub grpcbin = GRPCBinGrpc.newBlockingV2Stub(
            ManagedChannelBuilder
                    .forAddress("grpcb.in", 443)
                    .useTransportSecurity()
                    .build());


    public record HelloResponse(String message) {
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public HelloResponse hello() {
        final var response = springWithReact.sayHello(HelloRequest.newBuilder()
                .setName("Prabhu, %s".formatted(randomUUID()))
                .build());

        log.info("Response from localhost {}",response);

        final var grpcResponse = grpcbin.dummyUnary(Grpcbin.DummyMessage.newBuilder().build());
        log.info("Message from GRPC: {}", grpcResponse);

        return new HelloResponse(response.getMessage() + grpcResponse.getFString());
    }
}
