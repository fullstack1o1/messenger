package net.samitkumar.messenger.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@RequiredArgsConstructor
public class UserHandler {
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
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return ServerResponse.ok().body(userRepository.save(newUser));
    }
}
