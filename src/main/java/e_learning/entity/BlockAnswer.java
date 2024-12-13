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
public class BlockAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    double totalScore;

    @ManyToOne
    @JoinColumn(name = "response_id")
    private ResponseFormation response;

    private Long blockId;

    @ElementCollection
    private Map<String, Integer> answers;
}
