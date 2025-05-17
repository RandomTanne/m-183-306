package ch.gibb.yac.dtos.chat;

import jakarta.validation.constraints.NotNull;

public record TargetUsernameDTO(@NotNull(message = "Target username must not be null") String targetUsername) {
}
