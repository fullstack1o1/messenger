package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import net.samitkumar.messenger.controller.handlers.GroupHandler;
import net.samitkumar.messenger.controller.handlers.MessageHandler;
import net.samitkumar.messenger.controller.handlers.UserHandler;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@RestController
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", maxAge = 36000)
public class ApplicationController {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    @GetMapping("/whoami")
    User me(Authentication authentication) {
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        user.setPassword(null);
        return user;
    }

    @PostMapping("/signup")
    User signup(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Bean
    RouterFunction<ServerResponse> route(UserHandler userHandler, GroupHandler groupHandler, MessageHandler messageHandler) {
        return RouterFunctions.route()
                .GET("/me", accept(APPLICATION_JSON), userHandler::whoAmI)
                //.POST("/signup", contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)), userHandler::newUser)
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

