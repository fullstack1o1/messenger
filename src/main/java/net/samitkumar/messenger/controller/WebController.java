package net.samitkumar.messenger.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class WebController {
    @GetMapping("/register")
    String register() {
        return "forward:register.html";
    }

    @GetMapping("/sign-in")
    String customLogin() {
        return "forward:login.html";
    }

}
