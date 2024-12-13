package e_learning.entity;

import e_learning.enums.EvaluationType;
import e_learning.services.ServiceImpl.CourService;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<EvaluationBlock> blocks = new ArrayList<>();

    @ManyToOne
    private UserApp createdBy;

    @ManyToOne
    private Cour cour;

    private Date createdAt;
}
