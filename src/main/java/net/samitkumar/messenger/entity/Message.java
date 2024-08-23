package net.samitkumar.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private Long id;

    private Long messageTo;

    @Column("message_to_type")
    @Builder.Default
    private MessageToType messageToType = MessageToType.USER;

    private Long messageFrom;

    @ReadOnlyProperty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "dd-MM-yyyy")
    private LocalDate messageDate;

    @ReadOnlyProperty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "HH:mm:ss")
    private LocalTime messageTime;

    private String message;

    @Column("message_type")
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;
}
