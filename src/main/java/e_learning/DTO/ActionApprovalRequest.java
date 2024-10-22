package e_learning.DTO;

import lombok.Data;

@Data
public class ActionApprovalRequest {
    private Long actionId;  // Unique ID of the action (could be a participant, class, etc.)
    private String actionType;  // Type of action to approve (e.g., "ADD_PARTICIPANT", "MODIFY_CLASS")
    private Long entrepriseId;  // ID of the enterprise associated with this action

    // Getters and setters
}
