package e_learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BlockAnswer> blockAnswers;

    private Double totalScore;
}
