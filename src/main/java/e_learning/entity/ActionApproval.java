package e_learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long entrepriseId;
    private Long chargeFormationId;
    private Long responsableFormationId;
    private String actionType;
    private Long objectId;

    private boolean approved;
    private boolean rejected;
    private boolean updateRequested;

    @Column(length = 1000)
    private String comment;
    @CreationTimestamp
    private LocalDateTime createdDate;
    private LocalDateTime approvedDate;
    private LocalDateTime updateRequestedDate;
}
