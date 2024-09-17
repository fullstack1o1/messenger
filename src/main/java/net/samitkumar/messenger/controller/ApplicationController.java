package net.samitkumar.messenger.controller;

import net.samitkumar.messenger.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messenger")
public class ApplicationController {
    @GetMapping("/me")
    User me(Authentication authentication) {
        return authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
    }
}
