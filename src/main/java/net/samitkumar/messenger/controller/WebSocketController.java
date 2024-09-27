package net.samitkumar.messenger.controller;

import lombok.extern.slf4j.Slf4j;
import net.samitkumar.messenger.entity.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @MessageMapping("/public")
    void publicMessage(@Payload Message message) {
        log.info("public USER ");
    }

    @MessageMapping("/private")
    void privateMessage(@Payload Message message) {
        log.info("private USER ");
    }

    @MessageExceptionHandler
    public RuntimeException handleException(RuntimeException runtimeException) {
        return runtimeException;
    }
}
