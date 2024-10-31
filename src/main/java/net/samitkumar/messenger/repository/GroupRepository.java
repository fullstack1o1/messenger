package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Group;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends ListCrudRepository<Group, Long> {
    List<Group> findByCreatedBy(Long createdBy);
    Optional<Group> findByGroupIdAndCreatedBy(Long groupId, Long createdBy);
}
