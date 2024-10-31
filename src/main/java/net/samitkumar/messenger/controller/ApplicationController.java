package net.samitkumar.messenger.controller;

import lombok.RequiredArgsConstructor;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.handler.GroupHandler;
import net.samitkumar.messenger.handler.MessageHandler;
import net.samitkumar.messenger.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@Controller
@RequiredArgsConstructor
public class ApplicationController {

    @GetMapping("/register")
    String register() {
        return "forward:register.html";
    }

    @GetMapping("/sign-in")
    String customLogin() {
        return "forward:login.html";
    }

    @GetMapping("/who-am-i")
    @ResponseBody
    User me(Authentication authentication) {
        var user = authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
        user.setPassword(null);
        return user;
    }

    @GetMapping("/csrf")
    @ResponseBody
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @Bean
    RouterFunction<ServerResponse> route(UserHandler userHandler, GroupHandler groupHandler, MessageHandler messageHandler) {
        return RouterFunctions.route()
                .GET("/me", accept(APPLICATION_JSON), userHandler::whoAmI)
                .POST("/signup", contentType(APPLICATION_JSON).and(accept(APPLICATION_JSON)), userHandler::newUser)
                .GET("/groups/{groupId}/messages", request -> ServerResponse.noContent().build())
                .path("/users", userBuilder -> userBuilder
                        .GET("", accept(APPLICATION_JSON), userHandler::all)
                        .POST("", contentType(APPLICATION_JSON), userHandler::newUser)
                        .GET("/{userId}", accept(APPLICATION_JSON), userHandler::userById)
                        .path("/me/groups", groupUriBuilder -> groupUriBuilder
                                .GET("", accept(APPLICATION_JSON), groupHandler::fetchUserGroups)
                                .POST("", contentType(APPLICATION_JSON), groupHandler::newGroup)
                                .PATCH("/{groupId}", contentType(APPLICATION_JSON), groupHandler::patchGroup)
                                .DELETE("/{groupId}", contentType(APPLICATION_JSON), groupHandler::deleteGroup)
                                .GET("/{groupId}/unread-messages-count", contentType(APPLICATION_JSON), groupHandler::unreadMessagesCount)
                                .GET("/{groupId}/messages", accept(APPLICATION_JSON), messageHandler::groupMessages))
                        .path("/me/messages", messageUriBuilder -> messageUriBuilder
                                .GET("/{targetUserId}/unread-messages-count", messageHandler::unreadMessagesCount)
                                .GET("/{targetUserId}", accept(APPLICATION_JSON), messageHandler::messagesBetweenMeAndTargetUser))
                )
                .build();
    }
}

