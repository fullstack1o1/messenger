package net.samitkumar.messenger;

import net.samitkumar.messenger.entity.Group;
import net.samitkumar.messenger.entity.GroupMember;
import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.repository.GroupRepository;
import net.samitkumar.messenger.repository.MessageRepository;
import net.samitkumar.messenger.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MessageRepository messageRepository;

    @Test
    void entityTest() {
        var groupOne = groupRepository
                .save(Group.builder()
                        .groupName("Friends")
                        .createdBy(1L)
                        .members(Set.of(
                                GroupMember.builder()
                                        .userId(1L)
                                        .build(),
                                GroupMember.builder()
                                        .userId(2L)
                                        .build()
                        ))
                        .build());
        assertAll(
                () -> {
                    assertNotNull(groupOne.getGroupId());
                    System.out.println(groupOne);
                    //Group(groupId=1, groupName=Friends, createdBy=1, groupMembers=[GroupMember(groupId=null, userId=1, joinedAt=null), GroupMember(groupId=null, userId=2, joinedAt=null)], createdAt=null)
                },
                () -> {
                    groupRepository.findById(1L).ifPresent(System.out::println);
                    //Group(groupId=1, groupName=Friends, createdBy=1, groupMembers=[GroupMember(groupId=1, userId=1, joinedAt=2024-09-17T17:25:14.880689), GroupMember(groupId=1, userId=2, joinedAt=2024-09-17T17:25:14.880689)], createdAt=2024-09-17T17:25:14.880689)
                }
        );

        assertAll(
                //message to a user
                () -> {
                    //1L-2L
                    messageRepository.save(Message.builder()
                            .senderId(1L)
                            .receiverId(2L)
                            .content("Hello 2L")
                            .build());
                    //1L-3L
                    messageRepository.save(Message.builder()
                            .senderId(1L)
                            .receiverId(3L)
                            .content("Hello 3L")
                            .build());
                    //3L-1L
                    messageRepository.save(Message.builder()
                            .senderId(3L)
                            .receiverId(1L)
                            .content("Hello 1L")
                            .build());
                },
                //message to a group
                () -> {
                    messageRepository.save(Message.builder()
                            .senderId(1L)
                            .groupId(groupOne.getGroupId())
                            .content("Hello All")
                            .build());
                },
                () -> {
                    messageRepository.findAll().forEach(System.out::println);
                    /*
                        Message(messageId=1, senderId=1, receiverId=2, groupId=null, content=Hello 2L, createdAt=2024-09-17T17:36:05.995795)
                        Message(messageId=2, senderId=1, receiverId=3, groupId=null, content=Hello 3L, createdAt=2024-09-17T17:36:06.002014)
                        Message(messageId=3, senderId=3, receiverId=1, groupId=null, content=Hello 1L, createdAt=2024-09-17T17:36:06.006876)
                        Message(messageId=4, senderId=1, receiverId=null, groupId=1, content=Hello All, createdAt=2024-09-17T17:36:06.011083)
                     */
                },
                () -> {
                    System.out.println("### findMessageBySenderIdAndReceiverIdOrderByCreatedAt 1L-2L ###");
                    messageRepository.findAllMessagesBetweenTwoUsers(1L, 2L).forEach(System.out::println);
                    //Message(messageId=1, senderId=1, receiverId=2, groupId=null, content=Hello 2L, createdAt=2024-09-17T17:36:05.995795)

                    System.out.println("### findMessageBySenderIdAndReceiverIdOrderByCreatedAt 1L-3L ###");
                    messageRepository.findAllMessagesBetweenTwoUsers(1L, 3L).forEach(System.out::println);
                    /*
                    Message(messageId=2, senderId=1, receiverId=3, groupId=null, content=Hello 3L, createdAt=2024-09-17T17:36:06.002014)
                    Message(messageId=3, senderId=3, receiverId=1, groupId=null, content=Hello 1L, createdAt=2024-09-17T17:36:06.006876)
                     */
                },
                () -> {
                    System.out.println("### findMessagesByGroupIdOrderByCreatedAt 1L ###");
                    messageRepository.findMessagesByGroupIdOrderByCreatedAt(groupOne.getGroupId()).forEach(System.out::println);
                    //Message(messageId=4, senderId=1, receiverId=null, groupId=1, content=Hello All, createdAt=2024-09-17T17:36:06.011083)
                }
        );
    }

}
