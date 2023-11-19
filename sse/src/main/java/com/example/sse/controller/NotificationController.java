package com.example.sse.controller;

import com.example.sse.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notifications")
@RestController
public class NotificationController {

    private static AtomicInteger lastEventId = new AtomicInteger(1);
    private final NotificationService notificationService;

//    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<String>> getNotifications() {
//        return notificationService.getMessageFromSink()
//                .map(message -> {
//                    String id = String.valueOf(lastEventId.getAndIncrement());
//                    return ServerSentEvent
//                            .builder(message)
//                            .event("notification")
//                            .id(id)
//                            .comment("this is notification")
//                            .build();
//                })
//                .timeout(Duration.ofSeconds(100),Flux.empty())
//                .doOnComplete(() -> log.info("complete !!!"))
//                .doOnSubscribe(v ->log.info("subscribe!!!"));
//    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getNotifications(@RequestParam(required = false) String lastId) {
        return notificationService.getMessageFromSink(lastId);
    }

    @PostMapping
    public Mono<String> addNotification(@RequestBody Event event) {
        String notificationMessage = event.getType() + ": " + event.getMessage();
        notificationService.emitEvent(notificationMessage, String.valueOf(lastEventId.getAndIncrement()));
        return Mono.just("ok");
    }

    @GetMapping("/mono")
    public Mono<String> mymono() {

        return Mono.just("hello")
                .doOnNext(v -> log.info("mono : {}",v));
    }
    @GetMapping("/flux")
    public Flux<Integer> mylfux() {
        return Flux.range(0,30)
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(v -> log.info("flux : {}",v));
    }


}
