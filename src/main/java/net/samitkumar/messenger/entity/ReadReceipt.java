package net.samitkumar.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("read_receipts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadReceipt {
    @Id
    private Long id;
    private Long messageId;
    private Long userId;
}
