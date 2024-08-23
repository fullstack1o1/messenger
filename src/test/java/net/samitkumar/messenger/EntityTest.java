package net.samitkumar.messenger;

import net.samitkumar.messenger.entity.Group;
import net.samitkumar.messenger.entity.GroupMember;
import net.samitkumar.messenger.entity.Message;
import net.samitkumar.messenger.entity.MessageToType;
import net.samitkumar.messenger.repository.GroupRepository;
import net.samitkumar.messenger.repository.MessageRepository;
import net.samitkumar.messenger.repository.ReadReceiptRepository;
import net.samitkumar.messenger.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Autowired
    ReadReceiptRepository readReceiptRepository;

    @Test
    @Order(1)
    @DisplayName("User Entity Test")
    void userEntityTest() {
        assertAll(
                () -> userRepository.findAll().forEach(System.out::println),
                /*
                    User(id=1, name=Alice, email=alice@samitkumar.net)
                    User(id=2, name=Bob, email=bol@samitkumar.net)
                    User(id=3, name=Charlie, email=charlie@samitkumar.net)
                */
                () -> userRepository.findById(1L).ifPresent(System.out::println)
                /*
                    User(id=1, name=Alice, email=alice@samitkumar.net)
                * */
        );
    }

    @Test
    @Order(2)
    @DisplayName("Group Entity Test")
    void groupEntityTest() {
        assertAll(
                () -> groupRepository.saveAll(List.of(
                        Group.builder()
                                .name("School")
                                .members(
                                        Set.of(
                                            GroupMember.builder().userId(1L).build(),
                                            GroupMember.builder().userId(2L).build()
                                        )
                                )
                                .build(),
                        Group.builder()
                                .name("College")
                                .members(Set.of()).build()
                )).forEach(System.out::println),
                /*
                    Group(id=1, name=School, members=[GroupMember(groupId=null, userId=1), GroupMember(groupId=null, userId=2)])
                    Group(id=2, name=College, members=[])
                */
                () -> groupRepository.findAll().forEach(System.out::println),
                /*
                    Group(id=1, name=School, members=[GroupMember(groupId=1, userId=1), GroupMember(groupId=1, userId=2)])
                    Group(id=2, name=College, members=[])
                */
                () -> groupRepository.findById(1L).ifPresent(System.out::println)
                /*
                    Group(id=1, name=School, members=[GroupMember(groupId=1, userId=1), GroupMember(groupId=1, userId=2)])
                * */
        );
    }

    @Test
    @Order(3)
    @DisplayName("Message Entity Test")
    void messageEntityTest() {
        var users = userRepository.findAll();
        var groups = groupRepository.findAll();

        assertAll(
                () -> messageRepository.saveAll(List.of(
                        //conversation between user 1 and user 2
                        Message.builder().messageFrom(users.get(0).getId()).messageToType(MessageToType.USER).messageTo(users.get(1).getId()).message("Hello, Two!").build(),
                        Message.builder().messageFrom(users.get(1).getId()).messageToType(MessageToType.USER).messageTo(users.get(0).getId()).message("Hi One!").build(),
                        //conversation between user 1 and group 1
                        Message.builder().messageFrom(users.get(0).getId()).messageToType(MessageToType.GROUP).messageTo(groups.get(0).getId()).message("Hi, All!").build()
                )).forEach(System.out::println),
                /*
                    Message(id=1, messageTo=2, messageToType=USER, messageFrom=1, messageDate=null, messageTime=null, message=Hello, Two!, messageType=TEXT)
                    Message(id=2, messageTo=1, messageToType=USER, messageFrom=2, messageDate=null, messageTime=null, message=Hi One!, messageType=TEXT)
                    Message(id=3, messageTo=1, messageToType=GROUP, messageFrom=1, messageDate=null, messageTime=null, message=Hi, All!, messageType=TEXT)
                */
                () -> messageRepository.findAll().forEach(System.out::println),
                /*
                    Message(id=1, messageTo=2, messageToType=USER, messageFrom=1, messageDate=2024-08-23, messageTime=16:09:04, message=Hello, Two!, messageType=TEXT)
                    Message(id=2, messageTo=1, messageToType=USER, messageFrom=2, messageDate=2024-08-23, messageTime=16:09:04, message=Hi One!, messageType=TEXT)
                    Message(id=3, messageTo=1, messageToType=GROUP, messageFrom=1, messageDate=2024-08-23, messageTime=16:09:04, message=Hi, All!, messageType=TEXT)
                */
                () -> messageRepository.findAllByMessageFromAndMessageToAndMessageToType(1L, 2L, MessageToType.USER).forEach(System.out::println),
                () -> messageRepository.findAllByMessageToAndMessageFromAndMessageToType(1L, 2L, MessageToType.USER).forEach(System.out::println),
                /*
                    Message(id=1, messageTo=2, messageToType=USER, messageFrom=1, messageDate=2024-08-23, messageTime=16:31:58, message=Hello, Two!, messageType=TEXT)
                    Message(id=2, messageTo=1, messageToType=USER, messageFrom=2, messageDate=2024-08-23, messageTime=16:31:58, message=Hi One!, messageType=TEXT)
                */
                () -> messageRepository.findConversationBetweenUsers(1L, 2L, MessageToType.USER).forEach(System.out::println)
                /*
                    Message(id=1, messageTo=2, messageToType=USER, messageFrom=1, messageDate=2024-08-23, messageTime=16:35:55, message=Hello, Two!, messageType=TEXT)
                    Message(id=2, messageTo=1, messageToType=USER, messageFrom=2, messageDate=2024-08-23, messageTime=16:35:55, message=Hi One!, messageType=TEXT)
                */
        );
    }
}
