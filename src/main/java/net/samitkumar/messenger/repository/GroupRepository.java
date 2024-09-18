package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Group;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface GroupRepository extends ListCrudRepository<Group, Long> {
    List<Group> findByCreatedBy(Long createdBy);
}
