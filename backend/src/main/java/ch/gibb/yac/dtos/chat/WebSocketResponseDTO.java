package ch.gibb.yac.dtos.chat;

public class WebSocketResponseDTO {
    private final String type;
    private final String payload;

    public WebSocketResponseDTO(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}
