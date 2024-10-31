package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.model.UserEvent;
import net.samitkumar.messenger.model.UserMessage;
import net.samitkumar.messenger.repository.MessageRepository;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    final SimpMessagingTemplate simpMessagingTemplate;
    final MessageRepository messageRepository;
    final UserRepository userRepository;

    @MessageMapping("/public")
    @SendTo("/topic/queue")
    UserEvent publicMessage(@Payload UserMessage message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return UserEvent.builder().build();
    }

    @MessageMapping("/private")
    @SendToUser("/queue/private") // send him self
    UserEvent privateMessage(@Payload UserMessage message, Authentication authentication) {
        var user = getCurrentUser(authentication);

        Objects.requireNonNull(message.getReceiverId(), "ReceiverId should not be null");

        message.setSenderId(user.getUserId());
        var userEvent = UserEvent.builder().build();

        var messageTo = userRepository.findById(message.getReceiverId()).orElseThrow();
        log.info("private message from {} to {}", user, messageTo);
        simpMessagingTemplate.convertAndSendToUser(messageTo.getUsername(),"/queue/private", userEvent);
        return UserEvent.builder().build();
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException runtimeException) {
        log.error(runtimeException.getMessage());
    }

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
