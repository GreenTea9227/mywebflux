package yohan.myweblfux.reactor.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import yohan.myweblfux.common.repository.UserEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class FollowReactorRepository {
    private Map<String, Long> userFollowCountMap;

    public FollowReactorRepository() {
        userFollowCountMap = Map.of("1234", 1000L);
    }

    @SneakyThrows
    public Mono<Long> countByUserId(String userId) {
        return Mono.create(sink -> {
            log.info("FollowRepository.countByUserId: {}", userId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sink.success(userFollowCountMap.getOrDefault(userId, 0L));
        });
    }

    public Mono<Long> countWithUser() {
        return Mono.deferContextual(context -> {
            Optional<UserEntity> optionalUser = context.getOrEmpty("user");
            if (optionalUser.isEmpty())
                throw new RuntimeException("user not found");

            return Mono.just(optionalUser.get().getId());
        }).flatMap(this::countByUserId);
    }
}
