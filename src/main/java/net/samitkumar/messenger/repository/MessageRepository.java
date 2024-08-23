package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.entity.MessageToType;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface MessageRepository extends ListCrudRepository<Message, Long> {
    List<Message> findAllByMessageFromAndMessageToAndMessageToType(Long messageFrom, Long messageTo, MessageToType messageToType);

    List<Message> findAllByMessageToAndMessageFromAndMessageToType(Long messageTo, Long messageFrom, MessageToType messageToType);

    @Query("SELECT * FROM messages WHERE (message_from = :user1 AND message_to = :user2 AND message_to_type = :messageToType) OR (message_from = :user2 AND message_to = :user1 AND message_to_type = :messageToType)")
    List<Message> findConversationBetweenUsers(Long user1, Long user2, MessageToType messageToType);

    List<Message> findAllByMessageToAndMessageToType(Long messageTo, MessageToType messageToType);
}
