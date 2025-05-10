package ch.gibb.yac.handlers;

import ch.gibb.yac.models.Person;
import ch.gibb.yac.repositories.PersonRepository;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class HelloWebSocketHandler extends TextWebSocketHandler {

    private final PersonRepository personRepository;
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public HelloWebSocketHandler(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String username = Objects.requireNonNull(session.getPrincipal()).getName();
        Person person = personRepository.findByUsername(username);
        session.sendMessage(new TextMessage("Hello " + person.toString()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("pong"));
    }

    public void sendToUser(String username, String message) {
        sessions.stream()
                .filter(session ->
                        session.isOpen() &&
                                session.getPrincipal() != null &&
                                username.equals(session.getPrincipal().getName())
                )
                .findFirst()
                .ifPresent(session -> {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void sendToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
