package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.samitkumar.messenger.entity.Group;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.repository.GroupRepository;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

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
                .path("/user", builder -> builder
                        .GET("", accept(APPLICATION_JSON), userHandler::all)
                        .POST("", RequestPredicates.contentType(APPLICATION_JSON), userHandler::newUser)
                        .GET("/{userId}", accept(APPLICATION_JSON), userHandler::userById)
                        .GET("/{userId}/group", groupHandler::groupByUser)
                        .PUT("/{userId}", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{userId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                )
                .path("/group", builder -> builder
                        .GET("", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .POST("", RequestPredicates.contentType(APPLICATION_JSON), groupHandler::newGroup)
                        .GET("/{groupId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .PUT("/{groupId}", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{groupId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                )
                .path("/message", builder -> builder
                        .GET("", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .POST("", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .GET("/{messageId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .PUT("/{messageId}", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{messageId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
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
        System.out.println(newUser);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return ServerResponse.ok().body(userRepository.save(newUser));
    }
}

@Component
@RequiredArgsConstructor
class GroupHandler {
    final GroupRepository groupRepository;

    public ServerResponse groupByUser(ServerRequest request) {
        var userId = Long.parseLong(request.pathVariable("userId"));
        return ServerResponse.ok().body(groupRepository.findByCreatedBy(userId));
    }

    @SneakyThrows
    public ServerResponse newGroup(ServerRequest request) {
        var group = request.body(Group.class);
        return ServerResponse.ok().body(groupRepository.save(group));
    }
}

@Component
class MessageHandler {}