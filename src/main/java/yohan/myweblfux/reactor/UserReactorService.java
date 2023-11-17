package yohan.myweblfux.reactor;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import yohan.myweblfux.common.Article;
import yohan.myweblfux.common.EmptyImage;
import yohan.myweblfux.common.Image;
import yohan.myweblfux.common.User;
import yohan.myweblfux.common.repository.UserEntity;
import yohan.myweblfux.reactor.repository.ArticleReactorRepository;
import yohan.myweblfux.reactor.repository.FollowReactorRepository;
import yohan.myweblfux.reactor.repository.ImageReactorRepository;
import yohan.myweblfux.reactor.repository.UserReactorRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserReactorService {
    private final UserReactorRepository userRepository;
    private final ArticleReactorRepository articleRepository;
    private final ImageReactorRepository imageRepository;
    private final FollowReactorRepository followRepository;

    @SneakyThrows
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .flatMap(this::getUser);
    }

    @SneakyThrows
    private Mono<User> getUser(UserEntity userEntity) {
        Context context = Context.of("user", userEntity);
        var imageMono = imageRepository.findWithContext()
                .map(imageEntity ->
                        new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl())
                ).onErrorReturn(new EmptyImage())
                .contextWrite(context);


        var articlesMono = articleRepository.findAllWithContext()
                .skip(5)
                .take(2)
                .map(articleEntity -> new Article(articleEntity.getId(), articleEntity.getTitle(), articleEntity.getContent()))
                .collectList()
                .contextWrite(context);


        var followCountMono = followRepository
                .countWithUser()
                .contextWrite(context);

        return Mono.zip(imageMono, articlesMono, followCountMono)
                .map(resultList -> {
                            Image image = resultList.getT1();
                            List<Article> articles = resultList.getT2();
                            Long followCount = resultList.getT3();

                            Optional<Image> imageOptional = Optional.empty();
                            if (!(image instanceof EmptyImage)) {
                                imageOptional = Optional.of(image);
                            }
                            return new User(
                                    userEntity.getId(),
                                    userEntity.getName(),
                                    userEntity.getAge(),
                                    imageOptional,
                                    articles,
                                    followCount
                            );
                        }
                );

    }
}
