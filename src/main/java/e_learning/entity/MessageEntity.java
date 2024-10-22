package e_learning.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Date timestamp;
    private boolean isRead = false; // Default to false for new messages
    @ManyToOne
    private UserApp sender;
    @ManyToOne
    private UserApp receiver;

}
