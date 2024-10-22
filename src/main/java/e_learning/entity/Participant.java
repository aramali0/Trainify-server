package e_learning.entity;


import e_learning.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("participant")
public class Participant extends UserApp {

    @ManyToOne
    private HierarchicalUnit hierarchicalUnit;

    @ManyToOne
    private Entreprise entreprise;

    @ManyToMany
    @JoinTable(
            name = "participant_class",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<ClassEntity> classes = new ArrayList<>();

    @OneToMany(mappedBy = "participant", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unavailability> unavailabilities = new ArrayList<>();

    @OneToMany(mappedBy = "participant", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return getUserId() != null && getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
