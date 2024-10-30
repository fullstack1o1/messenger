package net.samitkumar.messenger.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table("groups")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    private Long groupId;

    @JsonAlias({"groupName", "username"})
    private String groupName;

    private Long createdBy;

    @MappedCollection(idColumn = "group_id")
    @Builder.Default
    private Set<GroupMember> members = new HashSet<>();

    @ReadOnlyProperty
    LocalDateTime createdAt;
}
