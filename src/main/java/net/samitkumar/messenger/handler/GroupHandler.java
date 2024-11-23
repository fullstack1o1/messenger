package net.samitkumar.messenger.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.samitkumar.messenger.entity.Group;
import net.samitkumar.messenger.entity.GroupMember;
import net.samitkumar.messenger.entity.User;
import net.samitkumar.messenger.model.InstantMessageResponse;
import net.samitkumar.messenger.repository.GroupRepository;
import net.samitkumar.messenger.repository.UserRepository;
import net.samitkumar.messenger.service.InstantMessageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class GroupHandler {
    final GroupRepository groupRepository;
    final InstantMessageService instantMessageService;
    final UserRepository userRepository;

    @SneakyThrows
    public ServerResponse newGroup(ServerRequest request) {
        var newGroup = request.body(Group.class);
        var user = getCurrentUser();
        newGroup.setCreatedBy(user.getUserId());

        if (nonNull(newGroup.getMembers()) && !newGroup.getMembers().isEmpty()) {
            newGroup.getMembers().add(GroupMember.builder().userId(user.getUserId()).build());
        } else {
            newGroup.setMembers(Set.of(GroupMember.builder().userId(user.getUserId()).build()));
        }

        //notify all the member of the group
        newGroup.getMembers().forEach(groupMember -> {
            userRepository.findById(groupMember.getUserId()).ifPresent(u -> instantMessageService.sendInstantMessage(InstantMessageResponse.Type.NEW_GROUP, newGroup.getCreatedBy(), u.getUsername(), "You have been added to a group"));
        });

        return ServerResponse.ok().body(groupRepository.save(newGroup));
    }

    public ServerResponse fetchUserGroups(ServerRequest request) {
        var user = getCurrentUser();
        var userOwnGroup = groupRepository.findByCreatedBy(user.getUserId());

        var memberPartOfTheGroup = groupRepository
                .findAll()
                .stream()
                .map(group -> {
                    if (!group.getCreatedBy().equals(user.getUserId()) &&
                            group.getMembers().stream().anyMatch(groupMember -> groupMember.getUserId().equals(user.getUserId()))) {
                        return group;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        userOwnGroup.addAll(memberPartOfTheGroup);

        return ServerResponse.ok().body(userOwnGroup);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? (User) authentication.getPrincipal() : User.builder().build();
    }

    @SneakyThrows
    public ServerResponse patchGroup(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        var dataToBeUpdated = request.body(Group.class);
        var groupInDb = groupRepository.findById(groupId).orElseThrow();

        if (nonNull(dataToBeUpdated.getGroupName())) {
            groupInDb.setGroupName(dataToBeUpdated.getGroupName());
        }

        if (nonNull(dataToBeUpdated.getMembers())) {
            groupInDb.getMembers().addAll(dataToBeUpdated.getMembers());
        }
        return ServerResponse.ok().body(groupRepository.save(groupInDb));
    }

    public ServerResponse deleteGroup(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        var user = getCurrentUser();
        groupRepository.findByGroupIdAndCreatedBy(groupId, user.getUserId()).ifPresent(groupRepository::delete);
        return ServerResponse.ok().build();
    }

    public ServerResponse unreadMessagesCount(ServerRequest request) {
        var groupId = Long.parseLong(request.pathVariable("groupId"));
        return ServerResponse.ok().body(Map.of("count",1));
    }
}
