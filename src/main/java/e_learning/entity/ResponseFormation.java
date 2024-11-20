package e_learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class ResponseFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EvaluationFormation evaluation;

    @ManyToOne
    private UserApp user;

    @ManyToOne
    private Entreprise entreprise;

    @ElementCollection
    private Map<String, Integer> answers;

    private Double percentage;
}
