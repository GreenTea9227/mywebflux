package com.example.websocket.config;

import com.example.websocket.handler.ChatWebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;

@Configuration
public class MappingConfig {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(ChatWebsocketHandler chatWebsocketHandler) {
        Map<String, WebSocketHandler> urlMapper = Map.of("/chat", chatWebsocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(1);
        mapping.setUrlMap(urlMapper);

        return mapping;
    }
}
