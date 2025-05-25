package io.madipalli.prabhu.SpringWithReact.service;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Log4j2
public class SubscriptionService {
    private final Map<String, CopyOnWriteArraySet<String>> stockNameToSessionIds = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> sessionIdToEmitter = new ConcurrentHashMap<>();

    public void addListener(final SseEmitter emitter, final String sessionId, final List<String> stockNames) {
        sessionIdToEmitter.put(sessionId, emitter);
        stockNames
                .forEach(
                        stockName -> {
                            stockNameToSessionIds
                                    .compute(stockName,
                                            (key, value) -> {

                                                if (value == null) {
                                                    return new CopyOnWriteArraySet<>(List.of(sessionId));
                                                }
                                                value.add(sessionId);
                                                return value;
                                            }
                                    );

                        }
                );
    }

    public void removeListener(final String sessionId) {
        sessionIdToEmitter.remove(sessionId);
    }

    public void broadcast(final String stockName, final Double value) {
        log.info("Broadcasting");
        final var sessionIdsOfStock = stockNameToSessionIds
                .get(stockName);
        if(sessionIdsOfStock == null) {
            log.info("{} stock is not subscribed.", stockName);
            return;
        }
        log.info("Found session ids {} for staock {}", String.join(",", sessionIdsOfStock), stockName);
        final var sessionIdsToRemove = sessionIdsOfStock
                .stream()
                .map(sessionId -> {
                    final var emitter = sessionIdToEmitter.get(sessionId);
                    if (emitter == null) {
                        return sessionId;
                    } else {
                        try {
                            emitter.send(
                                    SseEmitter.event()
                                            .name("priceUpdate")
                                            .data(PriceUpdate.builder()
                                                    .stockName(stockName)
                                                    .value(value)
                                                    .build())
                            );

                        } catch (IOException e) {
                            log.error("IO Exception while sending message to emitter for session id {}", sessionId, e);
                            throw new UncheckedIOException(e);
                        } catch (IllegalStateException e) {
                            log.error("The session seem to be closed hence removing the session id {}", sessionId, e);
                            return sessionId;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
        sessionIdsOfStock.removeAll(sessionIdsToRemove);

    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public void sendRandomEvent() {
        this.broadcast(String.valueOf((char) (secureRandom.nextInt(3) + 'A')), secureRandom.nextDouble(10, 100));
    }

    @Builder
    private record PriceUpdate(String stockName, Double value) {
    }
}
