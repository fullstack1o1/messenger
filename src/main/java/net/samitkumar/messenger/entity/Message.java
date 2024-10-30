package net.samitkumar.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("messages")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String content;
    @ReadOnlyProperty
    private LocalDateTime createdAt;
}