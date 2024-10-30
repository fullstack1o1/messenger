package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.MessageReadStatus;
import org.springframework.data.repository.ListCrudRepository;

public interface MessageReadStatusRepository extends ListCrudRepository<MessageReadStatus, Long> {
}
