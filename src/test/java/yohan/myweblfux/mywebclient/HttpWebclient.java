package yohan.myweblfux.mywebclient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpWebclient {

    @Test
    void t1() {
        WebClient webClient = WebClient.create("https://jsonplaceholder.typicode.com");

        Mono<String> responseMono = webClient
                .get()
                .uri("/todos/1")
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(String.class)
                .switchIfEmpty(Mono.error(new RuntimeException("error!!!!")));

        responseMono.subscribe(
                data -> log.info(data), // 데이터 처리
                error -> log.error("Error occurred: ", error) // 에러 처리
        );
        responseMono.subscribe(
                data -> log.info(data), // 데이터 처리
                error -> log.error("Error occurred: ", error) // 에러 처리
        );
        // 다른 로직 수행
        // 예: 로그 출력, 계산 등
        log.info("다른 작업을 수행 중...");

        // 데이터가 준비되면 결과 출력


        // 테스트가 종료되지 않도록 잠시 대기
        try {
            Thread.sleep(5000); // 적절한 대기 시간 설정
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void t2() {
        WebClient webClient = WebClient.create("https://jsonplaceholder.typicode.com");
//        WebClient.builder().defaultHeader("name","yohan");

        webClient
                .get()
                .uri("/todos")
                .accept(MediaType.ALL)
                .retrieve()
                .bodyToMono(String.class)
                .switchIfEmpty(Mono.error(new RuntimeException("error!!!!")));

    }
}
