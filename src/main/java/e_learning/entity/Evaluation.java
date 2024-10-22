package e_learning.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Long score;
    private Date createdAt;
    private Long timeTaken;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Quiz quiz;

    @ManyToOne
    private Participant participant;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Response> responses = new ArrayList<>();
}
