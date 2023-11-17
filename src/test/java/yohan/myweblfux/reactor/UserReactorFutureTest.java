package yohan.myweblfux.reactor;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yohan.myweblfux.common.User;
import yohan.myweblfux.reactor.repository.ArticleReactorRepository;
import yohan.myweblfux.reactor.repository.FollowReactorRepository;
import yohan.myweblfux.reactor.repository.ImageReactorRepository;
import yohan.myweblfux.reactor.repository.UserReactorRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class UserReactorFutureTest {
    UserReactorService userFutureService;
    UserReactorRepository userRepository;
    ArticleReactorRepository articleRepository;
    ImageReactorRepository imageRepository;
    FollowReactorRepository followRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserReactorRepository();
        articleRepository = new ArticleReactorRepository();
        imageRepository = new ImageReactorRepository();
        followRepository = new FollowReactorRepository();

        userFutureService = new UserReactorService(
                userRepository, articleRepository, imageRepository, followRepository
        );
    }

    @Test
    void getUserEmptyIfInvalidUserIdIsGiven() throws ExecutionException, InterruptedException {
        // given
        String userId = "invalid_user_id";

        // when
        Optional<User> user = userFutureService.getUserById(userId).blockOptional();

        // then
        assertTrue(user.isEmpty());
    }

    @Test
    void testGetUser() throws ExecutionException, InterruptedException {
        // given
        String userId = "1234";

        // when
        Optional<User> optionalUser = userFutureService.getUserById(userId).blockOptional();

        // then
        assertFalse(optionalUser.isEmpty());
        var user = optionalUser.get();
        assertEquals(user.getName(), "taewoo");
        assertEquals(user.getAge(), 32);

        assertFalse(user.getProfileImage().isEmpty());
        var image = user.getProfileImage().get();
        assertEquals(image.getId(), "image#1000");
        assertEquals(image.getName(), "profileImage");
        assertEquals(image.getUrl(), "https://dailyone.com/images/1000");

        assertEquals(2, user.getArticleList().size());

        assertEquals(1000, user.getFollowCount());
    }
}
