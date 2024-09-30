package net.samitkumar.messenger.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.entity.User;
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

import static java.util.Objects.nonNull;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    final SimpMessagingTemplate simpMessagingTemplate;
    final MessageRepository messageRepository;
    final UserRepository userRepository;

    @MessageMapping("/public")
    @SendTo("/topic/queue")
    Message publicMessage(@Payload Message message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return message;
    }

    @MessageMapping("/private")
    @SendToUser("/queue/private") // send him self
    Message privateMessage(@Payload Message message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();

        message.setSenderId(user.getUserId());
        if (nonNull(message.getGroupId())) {
            log.info("private group message from {} to {}", user, message.getGroupId());
            //message to group
            var savedMessage  = messageRepository.save(message);
            simpMessagingTemplate.convertAndSend("/topic/"+ message.getGroupId(), savedMessage);
            return savedMessage;
        } else {
            //message to user
            var savedMessage = messageRepository.save(message);
            var messageTo = userRepository
                    .findById(message.getReceiverId()).orElseThrow();
            log.info("private message from {} to {}", user, messageTo);
            simpMessagingTemplate.convertAndSendToUser(messageTo.getUsername(),"/queue/private", savedMessage);
            return savedMessage;
        }
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException runtimeException) {
        log.error(runtimeException.getMessage());
    }
}
