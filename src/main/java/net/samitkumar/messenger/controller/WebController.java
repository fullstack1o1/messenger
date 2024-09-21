package net.samitkumar.messenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
