package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.samitkumar.messenger.entity.Group;
import net.samitkumar.messenger.entity.GroupMember;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.repository.GroupRepository;
import net.samitkumar.messenger.repository.MessageRepository;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    @GetMapping("/whoami")
    User me(Authentication authentication) {
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        user.setPassword(null);
        return user;
    }

    @Bean
    RouterFunction<ServerResponse> route(UserHandler userHandler, GroupHandler groupHandler, MessageHandler messageHandler) {
        return RouterFunctions.route()
                .GET("/me", accept(APPLICATION_JSON), userHandler::whoAmI)
                .POST("/signup", contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)), userHandler::newUser)
                .path("/user", builder -> builder
                        .GET("", accept(APPLICATION_JSON), userHandler::all)
                        .POST("", contentType(APPLICATION_JSON), userHandler::newUser)
                        .GET("/{userId}", accept(APPLICATION_JSON), userHandler::userById)
                        .PUT("/{userId}", contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{userId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                )
                .path("/group", builder -> builder
                        .GET("", accept(APPLICATION_JSON), groupHandler::fetchUserGroups)
                        .POST("", contentType(APPLICATION_JSON), groupHandler::newGroup)
                        .PATCH("/{groupId}", contentType(APPLICATION_JSON), groupHandler::patchGroup)
                        .GET("/{groupId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .PUT("/{groupId}", contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{groupId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .GET("/{groupId}/messages", accept(APPLICATION_JSON), messageHandler::groupMessages)

                )
                .path("/message", builder -> builder
                        .GET("", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .POST("", contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .GET("/{messageId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .PUT("/{messageId}", contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{messageId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .GET("/between/me/and/{targetUserId}", accept(APPLICATION_JSON), messageHandler::messagesBetweenMeAndTargetUser)
                )
                .build();
    }
}

@Component
@RequiredArgsConstructor
class UserHandler {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    public ServerResponse whoAmI(ServerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        return ServerResponse.ok().body(user);
    }

    public ServerResponse all(ServerRequest request) {
        return ServerResponse.ok().body(userRepository.findAll());
    }

    public ServerResponse userById(ServerRequest request) {
        var userId = Long.parseLong(request.pathVariable("userId"));
        return userRepository.findById(userId)
                .map(user -> ServerResponse.ok().body(user))
                .orElse(ServerResponse.status(404).build());
    }
    @SneakyThrows
    public ServerResponse newUser(ServerRequest request) {
        var newUser = request.body(User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return ServerResponse.ok().body(userRepository.save(newUser));
    }
}

@Component
@RequiredArgsConstructor
class GroupHandler {
    final GroupRepository groupRepository;
    @SneakyThrows
    public ServerResponse newGroup(ServerRequest request) {
        var newGroup = request.body(Group.class);
        var user = getCurrentUser();
        newGroup.setCreatedBy(user.getUserId());

        //TODO try to maintain the createdUser in GroupMember Table
        if(nonNull(newGroup.getMembers()) && !newGroup.getMembers().isEmpty()) {
            newGroup.getMembers().add(GroupMember.builder().userId(user.getUserId()).build());
        } else {
            newGroup.setMembers(Set.of(GroupMember.builder().userId(user.getUserId()).build()));
        }

        return ServerResponse.ok().body(groupRepository.save(newGroup));
    }

    public ServerResponse fetchUserGroups(ServerRequest request) {
        var user = getCurrentUser();
        var userOwnGroup = groupRepository.findByCreatedBy(user.getUserId());

        var memberPartOfTheGroup = groupRepository
                .findAll()
                .stream()
                .map(group -> {
                    if(!group.getCreatedBy().equals(user.getUserId()) &&
                            group.getMembers().stream().anyMatch(groupMember -> groupMember.getUserId().equals(user.getUserId()))) {
                        return group;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userOwnGroup.addAll(memberPartOfTheGroup);

        return ServerResponse.ok().body(userOwnGroup);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
    }

    @SneakyThrows
    public ServerResponse patchGroup(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        var dataToBeUpdated = request.body(Group.class);
        var groupInDb = groupRepository.findById(groupId).orElseThrow();

        if(nonNull(dataToBeUpdated.getGroupName())) {
            groupInDb.setGroupName(dataToBeUpdated.getGroupName());
        }

        if(nonNull(dataToBeUpdated.getMembers())) {
            groupInDb.getMembers().addAll(dataToBeUpdated.getMembers());
        }
        return ServerResponse.ok().body(groupRepository.save(groupInDb));
    }
}

@Component
class MessageHandler {

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
        return ServerResponse.ok().body(messageRepository.findMessagesBetweenUsers(user.getUserId(), targetUserId));
    }

    public ServerResponse groupMessages(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        return ServerResponse
                .ok()
                .body(messageRepository.findMessagesInGroup(groupId));
    }
}