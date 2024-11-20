package e_learning.entity;

import e_learning.enums.EvaluationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EvaluationFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private EvaluationType type;

    @ElementCollection
    private List<String> questions;

    @ManyToOne
    private UserApp createdBy;

    @ManyToOne
    private Entreprise entreprise;

    private Date createdAt;
}
