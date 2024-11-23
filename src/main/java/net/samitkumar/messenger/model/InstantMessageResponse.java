package net.samitkumar.messenger.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InstantMessageResponse {
    Type type;
    Payload payload;

    public enum Type {
        MESSAGE,
        GROUP_MESSAGE,
        USER_CONNECT,
        USER_DISCONNECT,
        NEW_GROUP
    }

    @Builder
    @Data
    public static class Payload {
        private Type type;
        private Long fromUserId;
        private Long toUserId;
        private Long toGroupId;
        private String message;
        private LocalDateTime timestamp;
    }
}
