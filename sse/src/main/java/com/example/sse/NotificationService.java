package com.example.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
public class NotificationService {

    private final Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().replay().all();

    public Flux<ServerSentEvent<String>> getMessageFromSink(String lastReceivedId) {
        return sink.asFlux()
                .filter(event -> lastReceivedId == null || event.id().compareTo(lastReceivedId) > 0);
    }

    public void emitEvent(String data, String eventId) {
        ServerSentEvent<String> event = ServerSentEvent.builder(data)
                .id(eventId)
                .build();
        sink.tryEmitNext(event);
    }
}
