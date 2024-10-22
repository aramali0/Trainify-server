package e_learning.DTO;


import e_learning.enums.SatisfactionLevel;
import lombok.Data;

@Data
public class FeedbackDto {
    private Long id;
    private String organization;
    private String platformExperience;
    private String materials;
    private String participantGroup;
    private String assimilation;
    private String duration;
    private SatisfactionLevel satisfactionLevel;
    private String remarks;
    private Long formateurId; // to hold the reference to the Formateur
}
