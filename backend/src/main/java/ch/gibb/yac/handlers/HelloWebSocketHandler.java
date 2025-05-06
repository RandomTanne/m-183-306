package ch.gibb.yac.handlers;

import ch.gibb.yac.models.Person;
import ch.gibb.yac.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

public class HelloWebSocketHandler extends TextWebSocketHandler {

    private final PersonRepository personRepository;

    public HelloWebSocketHandler(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = Objects.requireNonNull(session.getPrincipal()).getName();
        Person person = personRepository.findByUsername(username);
        session.sendMessage(new TextMessage("Hello " + person.toString()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Optionally handle incoming messages
    }
}