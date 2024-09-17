package net.samitkumar.messenger;

import lombok.RequiredArgsConstructor;
import net.samitkumar.messenger.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

public class TestMessengerApplication {

	public static void main(String[] args) {
		SpringApplication.from(MessengerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
@EnableWebSecurity
@Configuration
class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//allow / and /register to be accessed without authentication
		http
				.authorizeHttpRequests(authorizeRequests ->
						authorizeRequests
								.requestMatchers("/", "/register").permitAll()
								.anyRequest().authenticated()
				)
				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults())
				.logout(Customizer.withDefaults());
		return http.build();
	}
}

@RequiredArgsConstructor
@Service
class UserService implements UserDetailsService {
	final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
	}
}