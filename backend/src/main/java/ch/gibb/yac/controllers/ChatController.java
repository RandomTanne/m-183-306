package ch.gibb.yac.controllers;

import ch.gibb.yac.dtos.chat.TargetUsernameDTO;
import ch.gibb.yac.exceptions.*;
import ch.gibb.yac.handlers.ChatWebSocketHandler;
import ch.gibb.yac.dtos.chat.ChatMessageRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatWebSocketHandler handler;

    public ChatController(ChatWebSocketHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestChat(@AuthenticationPrincipal User user, @RequestBody @Valid TargetUsernameDTO targetUsernameDTO) {
        try {
            handler.requestChat(user.getUsername(), targetUsernameDTO.targetUsername());
            return ResponseEntity.ok("You have successfully requested a chat with " + targetUsernameDTO.targetUsername());
        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong while trying to request a chat with the user " + targetUsernameDTO.targetUsername(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotConnectedException e) {
            return new ResponseEntity<>("The user " + targetUsernameDTO.targetUsername() + " is not connected", HttpStatus.NOT_FOUND);
        } catch (AlreadyHasRequestedChatException e) {
            return new ResponseEntity<>("You cannot request more than one chat at once", HttpStatus.BAD_REQUEST);
        } catch (CannotStartChatWithYourselfException e) {
            return new ResponseEntity<>("You cannot start a chat with yourself", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptChat(@AuthenticationPrincipal User user, @RequestBody @Valid TargetUsernameDTO targetUsernameDTO) {
        try {
            handler.acceptChat(user.getUsername(), targetUsernameDTO.targetUsername());
            return ResponseEntity.ok("You have successfully accepted a chat with " + targetUsernameDTO.targetUsername());
        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong while trying to accept a chat with the user " + targetUsernameDTO.targetUsername(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotConnectedException e) {
            return new ResponseEntity<>("The user " + targetUsernameDTO.targetUsername() + " is not connected", HttpStatus.NOT_FOUND);
        } catch (ChatNotRequestedException e) {
            return new ResponseEntity<>("The user " + targetUsernameDTO.targetUsername() + " has not requested a chat with you", HttpStatus.NOT_FOUND);
        } catch (AlreadyHasOngoingChatException e) {
            return new ResponseEntity<>("You cannot accept more than one chat at once", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendChat(@AuthenticationPrincipal User user, @RequestBody @Valid ChatMessageRequestDTO message) {
        try {
            handler.sendChat(user.getUsername(), message.targetUsername(), message.message());
            return ResponseEntity.ok("Message was sent successfully to the user " + message.targetUsername());
        } catch (IOException e) {
            return new ResponseEntity<>("Something went wrong while trying to send a message to the user " + message.targetUsername(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotConnectedException e) {
            return new ResponseEntity<>("The user " + message.targetUsername() + " is not connected", HttpStatus.NOT_FOUND);
        } catch (NoOngoingChatException e) {
            return new ResponseEntity<>("You don't have an ongoing chat with the user " + message.targetUsername(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelAllChats(@RequestBody @Valid TargetUsernameDTO targetUsernameDTO) {
        handler.cancelAllChats(targetUsernameDTO.targetUsername());
        return ResponseEntity.ok("Canceled all chats and chat requests");
    }
}
