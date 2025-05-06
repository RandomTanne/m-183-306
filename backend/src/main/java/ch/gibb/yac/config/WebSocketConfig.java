package ch.gibb.yac.config;

import ch.gibb.yac.handlers.HelloWebSocketHandler;
import ch.gibb.yac.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    PersonRepository personRepository;

    @Bean
    public HelloWebSocketHandler helloWebSocketHandler() {
        return new HelloWebSocketHandler(personRepository);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(helloWebSocketHandler(), "/hello").setAllowedOrigins("*");
    }
}