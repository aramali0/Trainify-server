package e_learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HierarchicalUnit {
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String name;

    private String type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private HierarchicalUnit parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<HierarchicalUnit> children;

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;
}
