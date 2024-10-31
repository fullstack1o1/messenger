package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.model.InstantMessageRequest;
import net.samitkumar.messenger.model.InstantMessageResponse;
import net.samitkumar.messenger.repository.GroupRepository;
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
    private final GroupRepository groupRepository;

    @MessageMapping("/public")
    @SendTo("/topic/queue")
    InstantMessageResponse publicMessage(@Payload InstantMessageRequest message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        return InstantMessageResponse.builder().build();
    }

    @MessageMapping("/private")
    @SendToUser("/queue/private") // send him self
    InstantMessageResponse privateMessage(@Payload InstantMessageRequest imRequest, Authentication authentication) {
        var user = getCurrentUser(authentication);
        Objects.requireNonNull(imRequest.getTo(), "to should not be null");

        if (Objects.equals(InstantMessageRequest.MessageType.USER, imRequest.getType())) {
            var messageToUser = userRepository.findById(imRequest.getTo()).orElseThrow();
            log.info("private USER-to-USER imRequest from USER: {} to USER: {}", user.getUserId(), messageToUser.getUserId());

            //save on db
            messageRepository.save(Message.builder()
                    .senderId(user.getUserId())
                    .receiverId(imRequest.getTo())
                    .content(imRequest.getContent())
                    .build());


            var userEvent = InstantMessageResponse.builder()
                    .type(InstantMessageResponse.Type.MESSAGE)
                    .payload(InstantMessageResponse.Payload.builder()
                            .fromUserId(user.getUserId())
                            .toUserId(imRequest.getTo())
                            .message(imRequest.getContent())
                            .build())
                    .build();

            //publish instant imRequest
            simpMessagingTemplate.convertAndSendToUser(
                    messageToUser.getUsername(),
                    "/queue/private",
                    userEvent);
            return userEvent;
        } else {
            var messageToGroup = groupRepository.findById(imRequest.getTo()).orElseThrow();
            log.info("private USER-to-GROUP imRequest from USER: {} to GROUP: {}", user.getUserId(), messageToGroup.getGroupId());

            //save on db
            messageRepository.save(Message.builder()
                    .senderId(user.getUserId())
                    .groupId(imRequest.getTo())
                    .content(imRequest.getContent())
                    .build());

            var groupIam = InstantMessageResponse.builder()
                    .type(InstantMessageResponse.Type.GROUP_MESSAGE)
                    .payload(InstantMessageResponse.Payload.builder()
                            .type(InstantMessageResponse.Type.GROUP_MESSAGE)
                            .fromUserId(user.getUserId())
                            .toUserId(messageToGroup.getGroupId())
                            .message(imRequest.getContent())
                            .build())
                    .build();

            //send to respective user
            messageToGroup.getMembers()
                    .stream()
                    .filter(member -> !Objects.equals(member.getUserId(), user.getUserId()))
                    .forEach(member -> {
                        simpMessagingTemplate.convertAndSendToUser(
                                userRepository.findById(member.getUserId()).orElseThrow().getUsername(),
                                "/queue/private",
                                groupIam
                                );
                    });

            return groupIam;
        }
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException runtimeException) {
        log.error(runtimeException.getMessage());
    }

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
