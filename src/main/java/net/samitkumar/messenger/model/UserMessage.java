package net.samitkumar.messenger.model;

import lombok.Builder;
import lombok.Data;
import net.samitkumar.messenger.entity.Message;

@Data
@Builder
public class UserMessage {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String content;
}
