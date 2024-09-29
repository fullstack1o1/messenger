package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Message;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends ListCrudRepository<Message, Long> {
    List<Message> findMessagesByGroupIdOrderByCreatedAt(Long groupId);
    List<Message> findMessageBySenderIdAndReceiverIdOrderByCreatedAt(Long senderId, Long receiverId);

}
