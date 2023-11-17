package yohan.myweblfux.reactor.repository;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import yohan.myweblfux.common.repository.ImageEntity;
import yohan.myweblfux.common.repository.UserEntity;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class ImageReactorRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageReactorRepository() {
        imageMap = Map.of(
                "image#1000", new ImageEntity("image#1000", "profileImage", "https://dailyone.com/images/1000")
        );
    }

    @SneakyThrows
    public Mono<ImageEntity> findById(String id) {
        return Mono.create(sink -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImageEntity image = imageMap.get(id);
            if (image == null)
                sink.error(new RuntimeException("image not found"));
            else
                sink.success(image);
        });

    }

    public Mono<ImageEntity> findWithContext() {
        return Mono.deferContextual(context -> {
            Optional<UserEntity> optionalUser = context.getOrEmpty("user");
            if (optionalUser.isEmpty())
                throw new RuntimeException("user not found");

            return Mono.just(optionalUser.get().getProfileImageId());
        }).flatMap(this::findById);
    }
}
