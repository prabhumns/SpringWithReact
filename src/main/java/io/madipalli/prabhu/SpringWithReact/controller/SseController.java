package io.madipalli.prabhu.SpringWithReact.controller;


import io.madipalli.prabhu.SpringWithReact.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Log4j2
@RequestMapping("/api/v1")
public class SseController {
    private final SubscriptionService service;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            final  @RequestParam @Valid @Size(min = 1, max = 5) List<@Pattern(regexp = "^[A-Z]$", message = "Each stock symbol must be a single uppercase letter") String> stocks,
            final @RequestParam @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", message = "Invalid UUID format") String sessionId) {
        log.info("Session {} is subscribing for {}", sessionId, String.join(",", stocks));
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        service.addListener(emitter, sessionId, stocks);

        final Runnable cleanup = () -> service.removeListener(sessionId);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(throwable -> {
            log.error("Error in session {}", sessionId, throwable);
        });

        return emitter;
    }
}
