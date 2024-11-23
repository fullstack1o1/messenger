package net.samitkumar.messenger.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstantMessageRequest {
    private MessageType type;
    private Long to;
    private String content;

    public enum MessageType { USER, GROUP }
}
