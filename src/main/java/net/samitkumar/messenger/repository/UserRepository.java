package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByUsername(String userName);
}
