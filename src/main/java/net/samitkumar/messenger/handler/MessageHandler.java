package net.samitkumar.messenger.handler;

import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.repository.MessageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class MessageHandler {

    private final MessageRepository messageRepository;

    public MessageHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
    }

    public ServerResponse messagesBetweenMeAndTargetUser(ServerRequest request) {
        var targetUserId = Long.parseLong(request.pathVariable("targetUserId"));
        var user = getCurrentUser();
        return ServerResponse.ok().body(messageRepository.findAllMessagesBetweenTwoUsers(user.getUserId(), targetUserId));
    }

    public ServerResponse groupMessages(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        return ServerResponse
                .ok()
                .body(messageRepository.findMessagesByGroupIdOrderByCreatedAt(groupId));
    }
}
