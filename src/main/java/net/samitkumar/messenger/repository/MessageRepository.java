package net.samitkumar.messenger.repository;

import net.samitkumar.messenger.entity.Message;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface MessageRepository extends ListCrudRepository<Message, Long>, ListPagingAndSortingRepository<Message, Long> {
    List<Message> findMessagesByGroupIdOrderByCreatedAt(Long groupId);

    @Query("""
        SELECT * FROM messages WHERE (sender_id = :senderId AND receiver_id = :receiverId) 
        OR (sender_id = :receiverId AND receiver_id = :senderId) 
        ORDER BY created_at ASC
    """)
    List<Message> findAllMessagesBetweenTwoUsers(Long senderId, Long receiverId);

}
