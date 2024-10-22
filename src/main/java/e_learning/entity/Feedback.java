package e_learning.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import e_learning.enums.SatisfactionLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organization;
    private String platformExperience;
    private String materials;
    private String participantGroup;
    private String assimilation;
    private String duration;

    @Enumerated(EnumType.STRING)
    private SatisfactionLevel satisfactionLevel;

    @Lob
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private UserApp userApp;
}
