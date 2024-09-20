package net.samitkumar.messenger.controller;

import net.samitkumar.messenger.entity.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @GetMapping("/register")
    String register() {
        return "forward:register.html";
    }

    @MessageMapping("/private")
    void privateMessage(@Payload Message message, Authentication authentication) {

    }

    @MessageMapping("/group")
    void groupMessage(@Payload Message message, Authentication authentication) {

    }

    @MessageMapping("/public")
    void publicMessage(@Payload Message message, Authentication authentication) {

    }

    @MessageExceptionHandler
    public RuntimeException handleException(RuntimeException runtimeException) {
        return runtimeException;
    }
}
