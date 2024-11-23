package net.samitkumar.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadStatus {
    Long messageId;
    Long userId;
    ReadStatus status;
    @ReadOnlyProperty
    LocalDateTime readAt;

    public enum ReadStatus { READ, UNREAD}
}
