package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Message;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends ListCrudRepository<Message, Long> {
    @Query("SELECT * FROM messages WHERE (sender_id = :user1 AND receiver_id = :user2) " +
            "OR (sender_id = :user2 AND receiver_id = :user1) ORDER BY created_at")
    List<Message> findMessagesBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query("SELECT * FROM messages WHERE group_id = :groupId ORDER BY created_at")
    List<Message> findMessagesInGroup(@Param("groupId") Long groupId);

}
