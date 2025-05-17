package ch.gibb.yac.dtos.chat;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ChatMessageRequestDTO(@NotNull(message = "Target username must not be null") String targetUsername, @NotNull(message = "Message must not be null") @Length(max = 4096, message = "Message cannot be longer than 4096 characters") String message) {
}
