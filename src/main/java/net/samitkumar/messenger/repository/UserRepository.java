package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.User;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {
}
