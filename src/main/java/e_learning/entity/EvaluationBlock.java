package e_learning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EvaluationBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double weightage;

    @ElementCollection
    private List<String> questions;

    @ManyToOne
    private EvaluationFormation evaluation;
}
