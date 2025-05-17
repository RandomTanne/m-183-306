package ch.gibb.yac.config;

import ch.gibb.yac.handlers.ChatWebSocketHandler;
import ch.gibb.yac.repositories.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    PersonRepository personRepository;

    public WebSocketConfig(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Bean
    public ChatWebSocketHandler chatRequestWebSocketHandler() {
        return new ChatWebSocketHandler(new ObjectMapper());
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatRequestWebSocketHandler(), "/sockets/chatrequests").setAllowedOrigins("*");
    }
}