package yohan.myweblfux.webfluxdeep;


import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
@RequestMapping("/greet")
public class GreetingController {

    @GetMapping("/name")
    Mono<String> hello(String name) {
        return Mono.just("hello world yohan").delayElement(Duration.ofMillis(5000));
    }

    @GetMapping("/void-shr")
    Mono<Void> monoVoid(ServerHttpResponse response) {
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap("hello world".getBytes()))
        );
    }

    @GetMapping("/redirect")
    Mono<Rendering> redirect() {
        var rendering = Rendering.redirectTo("/test")
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header("your", "yohan")
                .contextRelative(true)
                .propagateQuery(true)
                .build();
        return Mono.just(rendering);
    }

    @GetMapping("/hello-html")
    Mono<Rendering> helloHtml(String name) {
        var rendering = Rendering.view("hello")
                .modelAttribute("name", name)
                .status(HttpStatus.CREATED)
                .header("mine", "webflux")
                .build();

        return Mono.just(rendering);
    }

}
