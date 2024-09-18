package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import net.samitkumar.messenger.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/test/me")
    User me(Authentication authentication) {
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        user.setPasswordHash(null);
        return user;
    }

    @Bean
    RouterFunction<ServerResponse> route(UserHandler userHandler, GroupHandler groupHandler, MessageHandler messageHandler) {
        return RouterFunctions.route()
                .GET("/me", accept(APPLICATION_JSON), userHandler::whoAmI)
                .path("/user", builder -> builder
                        .GET("", accept(APPLICATION_JSON), userHandler::whoAmI)
                        .POST("", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .GET("/{userId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .PUT("/{userId}", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .DELETE("/{userId}", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                )
                .path("/group", builder -> builder
                        .GET("", accept(APPLICATION_JSON), request -> ServerResponse.noContent().build())
                        .POST("", RequestPredicates.contentType(APPLICATION_JSON), request -> ServerResponse.noContent().build())
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
    public ServerResponse whoAmI(ServerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        return ServerResponse.ok().body(user);
    }
}

@Component
class GroupHandler {}

@Component
class MessageHandler {}