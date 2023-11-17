package com.example.webfluximage.reactor.config;

import com.example.webfluximage.reactor.handler.ImageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    RouterFunction router(ImageHandler imageHandler) {
        return route()
                .path("/api",b1 -> b1
                        .path("/images",b2 ->b2
                                .GET("/{imageId:[0-9]+}",imageHandler::getImageById)))
                .build();
    }
}
