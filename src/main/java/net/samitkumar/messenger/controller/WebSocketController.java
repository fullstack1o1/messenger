package net.samitkumar.messenger.controller;

import lombok.extern.slf4j.Slf4j;
import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.entity.User;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @MessageMapping("/public")
    void publicMessage(@Payload Message message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        log.info("########public USER {} = {}", user, message);
    }

    @MessageMapping("/private")
    void privateMessage(@Payload Message message, Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        log.info("########private USER {} = {}", user, message);
    }

    @MessageExceptionHandler
    public void handleException(RuntimeException runtimeException) {
        log.error(runtimeException.getMessage());
    }
}
