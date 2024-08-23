package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Group;
import org.springframework.data.repository.ListCrudRepository;

public interface GroupRepository extends ListCrudRepository<Group, Long> {
}
