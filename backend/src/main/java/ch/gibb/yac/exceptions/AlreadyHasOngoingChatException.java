package ch.gibb.yac.exceptions;

public class AlreadyHasOngoingChatException extends Exception {
    public AlreadyHasOngoingChatException(String message) {
        super(message);
    }
}
