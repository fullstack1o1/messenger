package net.samitkumar.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("group_members")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GroupMember {
    private Long groupId;
    private Long userId;
}
