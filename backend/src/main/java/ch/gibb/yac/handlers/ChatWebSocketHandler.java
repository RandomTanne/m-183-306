package ch.gibb.yac.handlers;

import ch.gibb.yac.dtos.chat.WebSocketResponseDTO;
import ch.gibb.yac.exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    ObjectMapper objectMapper;

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, String> chatRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> ongoingChats = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        cancelAllChats(session.getPrincipal().getName());
        sessions.remove(session);
    }

    private void sendToUser(String targetUsername, String message) throws UserNotConnectedException, IOException {
        Optional<WebSocketSession> targetSession = sessions.stream()
                .filter(session ->
                        {
                            if (!session.isOpen()) return false;
                            return targetUsername.equals(session.getPrincipal().getName());
                        }
                )
                .findFirst();

        if(targetSession.isPresent()) {
            targetSession.get().sendMessage(new TextMessage(message));
        } else {
            throw new UserNotConnectedException("The requested user is not connected");
        }
    }

    private boolean chatWasRequested(String requestUsername, String targetUsername) {
        return chatRequests.containsKey(requestUsername) && chatRequests.get(requestUsername).equals(targetUsername);
    }

    private boolean chatIsOngoing(String requestUsername, String targetUsername) {
        return (ongoingChats.containsKey(requestUsername) && ongoingChats.get(requestUsername).equals(targetUsername)) ||
                (ongoingChats.containsKey(targetUsername) && ongoingChats.get(targetUsername).equals(requestUsername));
    }

    private boolean alreadyHasRequestedChat(String requestUsername) {
        return chatRequests.containsKey(requestUsername);
    }

    private boolean alreadyHasOngoingChat(String requestUsername) {
        return ongoingChats.containsKey(requestUsername);
    }

    public void requestChat(String requestUsername, String targetUsername) throws UserNotConnectedException, IOException, AlreadyHasRequestedChatException, CannotStartChatWithYourselfException {
        if(requestUsername.equals(targetUsername)) {
            throw new CannotStartChatWithYourselfException("The user cannot start a chat with himself");
        }
        if(alreadyHasRequestedChat(requestUsername)) {
            throw new AlreadyHasRequestedChatException("The user already has requested a chat");
        }

        String requestMessage = objectMapper.writeValueAsString(new WebSocketResponseDTO("request", null));
        sendToUser(targetUsername, requestMessage);
        chatRequests.put(requestUsername, targetUsername);
    }

    public void acceptChat(String requestUsername, String targetUsername) throws ChatNotRequestedException, UserNotConnectedException, IOException, AlreadyHasOngoingChatException {
        if(!chatWasRequested(targetUsername, requestUsername)) {
            throw new ChatNotRequestedException("The user has not requested a chat with you");
        }

        if(alreadyHasOngoingChat(requestUsername)) {
            throw new AlreadyHasOngoingChatException("The user already has an ongoing chat");
        }

        String acceptMessage = objectMapper.writeValueAsString(new WebSocketResponseDTO("accept", null));
        sendToUser(targetUsername, acceptMessage);

        chatRequests.remove(requestUsername);
        chatRequests.remove(targetUsername);
        ongoingChats.put(requestUsername, targetUsername);
        ongoingChats.put(targetUsername, requestUsername);
    }

    public void sendChat(String requestUsername, String targetUsername, String message) throws NoOngoingChatException, UserNotConnectedException, IOException {
        if(!chatIsOngoing(requestUsername, targetUsername)) {
            throw new NoOngoingChatException("The user has no ongoing chat with you");
        }

        String sendChatMessage = objectMapper.writeValueAsString(new WebSocketResponseDTO("message", message));
        sendToUser(targetUsername, sendChatMessage);
    }

    public void cancelAllChats(String requestUsername) {
        chatRequests.remove(requestUsername);

        String ongoingChat = ongoingChats.get(requestUsername);
        if(ongoingChat != null) {
            ongoingChats.remove(requestUsername);
            ongoingChats.remove(ongoingChat);
        }
    }
}
