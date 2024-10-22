package e_learning.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import e_learning.enums.Langue;
import e_learning.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String subTitre;
    private String description;
    private Date createdAt;
    private Date miseAJour;

    @Enumerated(EnumType.STRING)
    private Langue langue;

    private int duree;
    private boolean isApproved;
    private boolean isSent;
    private String imagePath; // Add this line to store the image path
    private boolean isFavorite;

    @ManyToOne
    private UserApp createdBy;

    @ManyToOne()
    private Entreprise entreprise;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cour_formateur",
            joinColumns = @JoinColumn(name = "cour_id"),
            inverseJoinColumns = @JoinColumn(name = "formateur_id")
    )
    private List<Formateur> formateurs;

    @ManyToMany
    @JoinTable(
            name = "cour_class",
            joinColumns = @JoinColumn(name = "cour_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<ClassEntity> classes;

    @OneToMany(mappedBy = "cour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference// Parent-side of the relationship
    private List<Session> sessions;

    @OneToMany(mappedBy = "cour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Library> libraries;

}
